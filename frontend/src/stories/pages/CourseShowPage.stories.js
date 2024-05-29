import React from 'react';
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import { coursesFixtures } from "fixtures/coursesFixtures";
import { rest } from "msw";

import CourseShowPage from "main/pages/CourseShowPage";

export default {
    title: 'pages/Course/CourseShowPage',
    component: CourseShowPage
};

const Template = () => <CourseShowPage/>;

export const OneItemsAdminUser = Template.bind({});

OneItemsAdminUser.parameters = {
    msw: [
        rest.get('/api/currentUser', (_req, res, ctx) => {
            return res( ctx.json(apiCurrentUserFixtures.adminUser));
        }),
        rest.get('/api/systemInfo', (_req, res, ctx) => {
            return res(ctx.json(systemInfoFixtures.showingNeither));
        }),
        rest.get('/api/courses/get', (_req, res, ctx) => {
            return res(ctx.json(coursesFixtures.oneCourse));
        }),
    ],
}