import React from 'react';
import GitHubAppTips from 'main/components/Courses/GitHubAppTips';
import { coursesFixtures } from 'fixtures/coursesFixtures';

export default {
    title: 'components/Courses/GitHubAppTips',
    component: GitHubAppTips
};


const Template = (args) => {
    return (
        <GitHubAppTips {...args} />
    )
};

export const Create = Template.bind({});

Create.args = {
    buttonLabel: "Create",
    submitAction: (data) => {
        console.log("Submit was clicked with data: ", data);
        window.alert("Submit was clicked with data: " + JSON.stringify(data));
    }
};

export const Update = Template.bind({ org: "StoryOrg", app: "StoryApp" });

Update.args = {
    initialContents: coursesFixtures.oneCourse,
    buttonLabel: "Update",
    submitAction: (data) => {
        console.log("Submit was clicked with data: ", data);
        window.alert("Submit was clicked with data: " + JSON.stringify(data));
    }
};