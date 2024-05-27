import { Card } from 'react-bootstrap';

export default function GitHubAppTips({ app }) {
    return (<>
        <Card style={{ marginBottom: 20, marginTop: 20 }}>
            <Card.Body>
                <Card.Title>Warning: Your organization does not seem to have our GitHub App installed</Card.Title>
                <Card.Text>
                    <ul>
                        <li>
                            After you click <a href={"https://github.com/apps/"+app} target='_blank'>this link</a>, please install/configure and select your course organization to install it.
                        </li>
                        <li>
                            If you are successful you'll see the text "Okay, UCSB CS Linker was installed on the @ucsb-cs156-f24 account".
                        </li>
                        <li>
                            Then you can return to this app.
                        </li>
                    </ul>
                    The GitHub App needs to be installed is: {app}
                </Card.Text>
            </Card.Body>
        </Card>
    </>)
}