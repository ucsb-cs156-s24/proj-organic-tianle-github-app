import { Card } from 'react-bootstrap';

export default function GitHubAppTips({ githubStatus, org }) {
    console.log("org=", org, "githubStatus=", githubStatus);

    if (githubStatus?.exceptionThrown)
        return (
            <div data-testid="CourseEdit-githubAppTips">
                <Card style={{ marginBottom: 20, marginTop: 20 }} data-testid="CourseEdit-GHAT-Card-Error">
                    <Card.Body>
                        <Card.Title>Warning: An error occurred while trying to check the GitHub App status</Card.Title>
                        <ul>
                            <li>
                                The error was: <code>{githubStatus.exceptionMessage}</code>
                            </li>
                            <li>
                                Please contact your administrator to check the configuration of the app.
                            </li>
                        </ul>
                    </Card.Body>
                </Card>
            </div>
        );

    if (githubStatus && !(githubStatus.name))
        return (<div data-testid="CourseEdit-githubAppTips">
            <Card style={{ marginBottom: 20, marginTop: 20 }} data-testid="CourseEdit-GHAT-Card-Error">
                <Card.Body>
                    <Card.Title>Warning: This app does not seem to be configured properly.</Card.Title>
                    <ul>
                        <li>
                            The value of <code>githubStatus.name</code> is "<code>{githubStatus.name}</code>"
                        </li>
                        <li>
                            The value of <code>app</code> should not be blank; it should be the name of the GitHub App associcated
                            with the client id, client secret and private key configured by the administrator
                        </li>
                        <li>
                            Please contact your administrator to check the configuration of the app.
                        </li>
                    </ul>
                </Card.Body>
            </Card>
        </div>
        );

    const app = githubStatus.name;

    return (<div data-testid="CourseEdit-githubAppTips">
        <Card style={{ marginBottom: 20, marginTop: 20 }} data-testid="CourseEdit-GHAT-Card">
            <Card.Body>
                <Card.Title>Warning: Your organization does not seem to have our GitHub App installed</Card.Title>
                <ul>
                    <li>
                        After you click <a href={"https://github.com/apps/" + app} target='_blank' data-testid="CourseEdit-GHAT-Link" rel="noopener noreferrer">this link</a>, please install/configure and select your course organization to install it.
                    </li>
                    <li>
                        If you are successful you'll see the text "Okay, UCSB CS Linker was installed on the @{org}".
                    </li>
                    <li>
                        Then you can return to this app.
                    </li>
                </ul>
                The GitHub App that needs to be installed is: {app}
            </Card.Body>
        </Card>
    </div>
    );
}