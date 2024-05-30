import subprocess
import json
import os

def execute_command(command):
    try:
        result = subprocess.run(command, shell=True, capture_output=True, text=True)
        exit_code = result.returncode
        stdout = result.stdout.strip()
        stderr = result.stderr.strip()
        return exit_code, stdout, stderr
    except Exception as e:
        return -1, '', str(e)
    
def getInpurOrDefault(prompt, default=''):
    value = input(prompt + " [" + default + "]: "if default!='' else prompt + ": ")
    if value == '':
        return default
    return value

def readJsonConf():
    try:
        with open(".deploy.json", 'r') as file:
            return json.load(file)
    except Exception as e:
        print("Error reading config file .deploy.json")
        print(str(e))
        return None
def writeJsonConf(config):
    try:
        with open(".deploy.json", 'w') as file:
            json.dump(config, file)
    except Exception as e:
        print("Error writing config file .deploy.json")
        print(str(e))

def assertAppExists(appName):
    exit_code, stdout, stderr = execute_command("dokku apps:exists "+appName)
    if exit_code != 0:
        exit_code, stdout, stderr = execute_command("dokku apps:create "+appName)
        if exit_code != 0:
            print("Error creating application: " + appName)
            print(stderr)
            exit(1)

def assertDBExists(DBName):
    exit_code, stdout, stderr = execute_command("dokku postgres:exists "+DBName)
    if exit_code != 0:
        print("DB does not exist: " + DBName, "Creating database")
        exit_code, stdout, stderr = execute_command("dokku postgres:create "+DBName)
        if exit_code != 0:
            print("Error creating database: " + DBName)
            print(stderr)
            exit(1)

def getDBCredentials(DBName):
    exit_code, stdout, stderr = execute_command("dokku postgres:info "+DBName)
    if exit_code != 0:
        print("Error getting database info: " + DBName)
        print(stderr)
        exit(1)
    lines = stdout.split("\n")
    for line in lines:
        if "Dsn" in line:
            return line.split("Dsn:")[1].strip()
    return None

def assertDBLink(appName, DBName):
    exit_code, stdout, stderr = execute_command("dokku postgres:links "+DBName)
    if exit_code != 0:
        print("Error getting db links: " + DBName)
        print(stderr)
        exit(1)
    if appName not in stdout:
        exit_code, stdout, stderr = execute_command("dokku postgres:link "+DBName+" "+appName)
        if exit_code != 0:
            print("Error linking database: " + DBName + " to application: " + appName)
            print(stderr)
            exit(1)
def assertMount(appName, local, target):
    exit_code, stdout, stderr = execute_command("dokku storage:list "+appName)
    if exit_code != 0:
        print("Error getting mounts: " + appName)
        print(stderr)
        exit(1)
    if target not in stdout:
        mountPath = local+":"+target
        exit_code, stdout, stderr = execute_command("dokku storage:mount "+appName+" "+mountPath)
        if exit_code != 0:
            print("Error mounting path: " + mountPath + " to application: " + appName)
            print(stderr)
            exit(1)
            
def setEnvVar(appName, key, value):
    exit_code, stdout, stderr = execute_command("dokku config:set --no-restart "+appName+" "+key+"="+value)
    if exit_code != 0:
        print("Error setting environment variable: " + key + " for application: " + appName)
        print(stderr)

def configApp(config):
    # Set environment variables
    print("Setting environment variables for application: " + config["applicationName"])
    setEnvVar(config["applicationName"], "GITHUB_CLIENT_ID", config["githubAccessKey"])
    setEnvVar(config["applicationName"], "GITHUB_CLIENT_SECRET", config["githubSecretKey"])
    setEnvVar(config["applicationName"], "ADMIN_GITHUB_LOGINS", config["adminUsers"])
    
    # print(config["dbURL"])
    JDBC_DATABASE_URL = "jdbc:postgresql://"+config["dbURL"].split("@")[1]
    JDBC_DATABASE_PASSWORD = config["dbURL"].split(":")[2].split("@")[0]
    setEnvVar(config["applicationName"], "JDBC_DATABASE_URL", JDBC_DATABASE_URL)
    setEnvVar(config["applicationName"], "JDBC_DATABASE_USERNAME", "postgres")
    setEnvVar(config["applicationName"], "JDBC_DATABASE_PASSWORD", JDBC_DATABASE_PASSWORD)
    
    setEnvVar(config["applicationName"], "PRODUCTION", "true")
    
    # Convert github private key to der          openssl pkcs8 -topk8 -inform PEM -outform DER -in      $1                        -out  $1.der    -nocrypt
    exit_code, stdout, stderr = execute_command("openssl pkcs8 -topk8 -inform PEM -outform DER -in "+config["githubPrivateKey"]+" -out .github.pk.der -nocrypt")
    if exit_code != 0:
        print("Error converting github private key to der")
        print(stderr)
        exit(1)
    # Get current directory
    cwd = os.getcwd()
    # Load key into application
    assertMount(config["applicationName"], cwd+"/.github.pk.der", "/app/.github.pk.der")
    setEnvVar(config["applicationName"], "GITHUB_PKFILE", "/app/.github.pk.der")
    
    # Setup git message
    exit_code, stdout, stderr = execute_command("dokku git:set "+config["applicationName"]+" keep-git-dir true")
    if exit_code != 0:
        print("Error setting git message")
        print(stderr)
        exit(1)
    setEnvVar(config["applicationName"], "SOURCE_REPO", config["githubRepoURL"])
    
def pullGitRepo(appName, repo, branch):
    print("Pulling git repository: " + repo + " # " + branch)
    exit_code, stdout, stderr = execute_command("dokku git:sync "+appName+" "+repo+" "+branch)
    if exit_code != 0:
        print("Error pulling git repository: " + repo + " # " + branch)
        print(stderr)
        exit(1)
    
def rebuildApp(appName):
    print("Rebuilding application: " + appName, "This may take a while")
    exit_code, stdout, stderr = execute_command("dokku ps:rebuild "+appName)
    if exit_code != 0:
        print("Error rebuilding application: " + appName)
        print(stderr)
        exit(1)
            
def main():
    config = readJsonConf()
    if config is None:
        applicationName = getInpurOrDefault("Enter the application name you want to deploy to", "organic")
        databaseName = getInpurOrDefault("Enter the database name you want to deploy to", "organic-db")
        githubRepoURL = getInpurOrDefault("Enter the github repository URL")
        githubBranch = getInpurOrDefault("Enter the github branch name", "main")
        githubAccessKey = getInpurOrDefault("Enter the github access key")
        githubSecretKey = getInpurOrDefault("Enter the github secret key")
        githubPrivateKey = getInpurOrDefault("Enter the path to github private key (*.pem)")
        adminUsers = getInpurOrDefault("Enter the github usernames of admin users (comma separated)")
        config = {
            "applicationName": applicationName,
            "databaseName": databaseName,
            "githubRepoURL": githubRepoURL,
            "githubBranch": githubBranch,
            "githubAccessKey": githubAccessKey,
            "githubSecretKey": githubSecretKey,
            "githubPrivateKey": githubPrivateKey,
            "adminUsers": adminUsers,
            "dbURL": "",
        }
        writeJsonConf(config)
        print("You config has been saved to .deploy.json, please feel free to modify it if needed. However, do NOT commit this file to your repository.")
    print("Deploying application: " + config["applicationName"]+" from github repository: "+config["githubRepoURL"]+" # "+config["githubBranch"])
    
    assertAppExists(config["applicationName"])
    assertDBExists(config["databaseName"])
    
    dbURL = getDBCredentials(config["databaseName"])
    if dbURL is None:
        print("Error getting database credentials: Dsn not found")
        exit(1)
    config["dbURL"] = dbURL
    writeJsonConf(config)
    
    assertDBLink(config["applicationName"], config["databaseName"])
    
    configApp(config)
    
    pullGitRepo(config["applicationName"], config["githubRepoURL"], config["githubBranch"])
    
    # rebuildApp(config["applicationName"])
    print("Setup complete, please run the following command to rebuild the application")
    print("     dokku ps:rebuild "+config["applicationName"])
    
if __name__ == '__main__':
    main()