import React from 'react';
import GitHubAppTips from 'main/components/Courses/GitHubAppTips';

export default {
    title: 'components/Courses/GitHubAppTips',
    component: GitHubAppTips
};


const Template = (args) => {
    return (
        <GitHubAppTips {...args} />
    )
};

export const Empty = Template.bind({});

Empty.args = {
    org: "", app: ""
};

export const StoryApp = Template.bind({});

StoryApp.args = {
    org: "StoryOrg", app: "StoryApp"
};