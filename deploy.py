import subprocess
import json

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
    exit_code, stdout, stderr = execute_command("dokku apps "+appName)
    print(exit_code, stderr)
        

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
        config = {
            "applicationName": applicationName,
            "databaseName": databaseName,
            "githubRepoURL": githubRepoURL,
            "githubBranch": githubBranch,
            "githubAccessKey": githubAccessKey,
            "githubSecretKey": githubSecretKey,
            "githubPrivateKey": githubPrivateKey
        }
        writeJsonConf(config)
        print("You config has been saved to .deploy.json, please feel free to modify it if needed. However, do NOT commit this file to your repository.")
    print("Deploying application: " + config["applicationName"]+" from github repository: "+config["githubRepoURL"]+" # "+config["githubBranch"])
    assertAppExists(config["applicationName"])
if __name__ == '__main__':
    main()