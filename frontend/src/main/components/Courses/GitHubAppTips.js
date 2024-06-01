import { Card } from 'react-bootstrap';

export default function GitHubAppTips({ app, org }) {
    if (!app ) return (<div data-testid="CourseEdit-githubAppTips">
        <Card style={{ marginBottom: 20, marginTop: 20 }} data-testid="CourseEdit-GHAT-Card-Error">
            <Card.Body>
                <Card.Title>Warning: This app does not seem to be configured properly.</Card.Title>
                <Card.Text>
                    <ul>
                        <li>
                            The value of <code>app</code> is "<code>{app}</code>"
                        </li>
                        <li>
                            The value of <code>app</code> should not be blank; it should be the name of the GitHub App associcated
                            with the client id, client secret and private key configured by the administrator
                        </li>
                        <li>
                            Please contact your administrator to check the configuration of the app.
                        </li>
                    </ul>
                </Card.Text>
            </Card.Body>
        </Card>
    </div>
    );

    return (<div data-testid="CourseEdit-githubAppTips">
        <Card style={{ marginBottom: 20, marginTop: 20 }} data-testid="CourseEdit-GHAT-Card">
            <Card.Body>
                <Card.Title>Warning: Your organization does not seem to have our GitHub App installed</Card.Title>
                <Card.Text>
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
                    The GitHub App needs to be installed is: {app}
                </Card.Text>
            </Card.Body>
        </Card>
    </div>)
}