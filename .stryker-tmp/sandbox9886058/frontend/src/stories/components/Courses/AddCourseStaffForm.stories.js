import React from 'react';
import AddCourseStaffForm from 'main/components/Courses/AddCourseStaffForm';
import { addCourseStaffFixtures } from 'fixtures/addCourseStaffFixtures';

export default {
    title: 'components/Courses/AddCourseStaffForm',
    component: AddCourseStaffForm
};


const Template = (args) => {
    return (
        <AddCourseStaffForm {...args} />
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

export const Update = Template.bind({});

Update.args = {
    initialContents: addCourseStaffFixtures.oneCourseStaff,
    buttonLabel: "Update",
    submitAction: (data) => {
        console.log("Submit was clicked with data: ", data); 
        window.alert("Submit was clicked with data: " + JSON.stringify(data));
   }
};