import { fireEvent, render, waitFor, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter, Routes, Route } from "react-router-dom";
import CoursesStaffPage from "main/pages/CoursesStaffPage";


import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";
import mockConsole from "jest-mock-console";

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

describe("CourseStaffPage tests", () => {

    const axiosMock = new AxiosMockAdapter(axios);

    const testId = "StaffTable";

    const setupAdminUser = () => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.adminUser);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    };

    const setupInstructorUser = () => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.instructorUser);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    }

    const setupUser = () => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    }

    test("Renders with Add Staff Button for admin user", async () => {
        // arrange
        setupAdminUser();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/courses/getStaff").reply(200, []);

        // act
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter initialEntries={['/courses/1/staff']}>
                    <Routes>
                        <Route path="/courses/:courseId/staff" element={<CoursesStaffPage />} />
                    </Routes>
                </MemoryRouter>
            </QueryClientProvider>
        );

        // assert
        await waitFor( ()=>{
            expect(screen.getByText(/Add Staff/)).toBeInTheDocument();
        });
        const button = screen.getByText(/Add Staff/);
        expect(button).toHaveAttribute("href", "/courses/1/staff/addStaff");
        expect(button).toHaveAttribute("style", "float: right;");
    });

    test("Renders with Add Staff Button for instructor user", async () => {
        // arrange
        setupInstructorUser();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/courses/getStaff").reply(200, []);

        // act
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter initialEntries={['/courses/1/staff']}>
                    <Routes>
                        <Route path="/courses/:courseId/staff" element={<CoursesStaffPage />} />
                    </Routes>
                </MemoryRouter>
            </QueryClientProvider>
        );

        // assert
        await waitFor( ()=>{
            expect(screen.getByText(/Add Staff/)).toBeInTheDocument();
        });
        const button = screen.getByText(/Add Staff/);
        expect(button).toHaveAttribute("href", "/courses/1/staff/addStaff");
        expect(button).toHaveAttribute("style", "float: right;");
    });
    
    test("Renders without Create Button for non admin and non instructor user", async () => {
        // arrange
        setupUser(); 
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/courses/getStaff").reply(200, []);
    
        // act
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter initialEntries={['/courses/1/staff']}>
                    <Routes>
                        <Route path="/courses/:courseId/staff" element={<CoursesStaffPage />} />
                    </Routes>
                </MemoryRouter>
            </QueryClientProvider>
        );
    
        // assert
        await waitFor(() => {
            expect(screen.queryByText(/Add Staff/)).not.toBeInTheDocument();
        });
    });
    

    test("renders empty table when backend unavailable, admin", async () => {
        // arrange
        setupAdminUser();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/courses/getStaff").timeout();
        const restoreConsole = mockConsole();

        // act
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter initialEntries={['/courses/1/staff']}>
                    <Routes>
                        <Route path="/courses/:courseId/staff" element={<CoursesStaffPage />} />
                    </Routes>
                </MemoryRouter>
            </QueryClientProvider>
        );

        // assert
        await waitFor(() => { expect(axiosMock.history.get.length).toBeGreaterThanOrEqual(1); });

        restoreConsole();

        expect(screen.queryByTestId(`${testId}-cell-row-0-col-id`)).not.toBeInTheDocument();
    });

    test("renders empty table when backend unavailable, instructor", async () => {
        // arrange
        setupInstructorUser();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/courses/getStaff").timeout();
        const restoreConsole = mockConsole();

        // act
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter initialEntries={['/courses/1/staff']}>
                    <Routes>
                        <Route path="/courses/:courseId/staff" element={<CoursesStaffPage />} />
                    </Routes>
                </MemoryRouter>
            </QueryClientProvider>
        );

        // assert
        await waitFor(() => { expect(axiosMock.history.get.length).toBeGreaterThanOrEqual(1); });

        restoreConsole();

        expect(screen.queryByTestId(`${testId}-cell-row-0-col-id`)).not.toBeInTheDocument();
    });

    

});


