import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import CoursesAddStaffPage from "main/pages/CoursesAddStaffPage";

import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";

import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

const mockToast = jest.fn();
jest.mock('react-toastify', () => {
    const originalModule = jest.requireActual('react-toastify');
    return {
        __esModule: true,
        ...originalModule,
        useParams: () => ({ courseId: '1' }),
        toast: (x) => mockToast(x)
    };
});

const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => {
    const originalModule = jest.requireActual('react-router-dom');
    return {
        __esModule: true,
        ...originalModule,
        Navigate: (x) => { mockNavigate(x); return null; }
    };
});

describe("CourseAddStaffPage tests", () => {

    const axiosMock =new AxiosMockAdapter(axios);

    beforeEach(() => {
        jest.clearAllMocks();
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    });

    
    test("renders without crashing", () => {
        const queryClient = new QueryClient();
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter initialEntries={['/courses/1/addStaff']}>
                    <Routes>
                        <Route path="/courses/:courseId/addStaff" element={<CoursesAddStaffPage />} />
                    </Routes>
                </MemoryRouter>
            </QueryClientProvider>
        );
    });

    test("on submit, makes request to backend", async () => {

        const queryClient = new QueryClient();
        const staff = {
            id: 1,
            courseId: "1",
            githubLogin: "pconrad"
        };

        axiosMock.onPost("/api/courses/addStaff").reply(202, staff);

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter initialEntries={['/courses/1/addStaff']}>
                    <Routes>
                        <Route path="/courses/:courseId/addStaff" element={<CoursesAddStaffPage />} />
                    </Routes>
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(() => {
            expect(screen.getByTestId("AddCourseStaffForm-courseId")).toBeInTheDocument();
        });

        const courseIdField = screen.getByTestId("AddCourseStaffForm-courseId");
        const githubIdField = screen.getByTestId("AddCourseStaffForm-githubId");
        const submitButton = screen.getByTestId("AddCourseStaffForm-submit");
        

        fireEvent.change(courseIdField, { target: { value: '1' } });
        fireEvent.change(githubIdField, { target: { value: 'pconrad' } });

        expect(submitButton).toBeInTheDocument();

        fireEvent.click(submitButton);

        await waitFor(() => expect(axiosMock.history.post.length).toBe(1));

        expect(axiosMock.history.post[0].params).toEqual(
            {
                "courseId": "1",
                "githubLogin": "pconrad"
        });

        await waitFor(() => expect(mockToast).toBeCalled());
        expect(mockToast).toBeCalledWith("New staff added - courseId: 1");
        expect(mockNavigate).toBeCalledWith({ "to": "/courses/1/staff" });
    });


});

