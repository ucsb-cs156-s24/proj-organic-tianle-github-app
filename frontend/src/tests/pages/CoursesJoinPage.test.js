import { fireEvent, render, waitFor, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import CourseJoinPage from "main/pages/CourseJoinPage";
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import { coursesFixtures } from "fixtures/coursesFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

const mockToast = jest.fn();
jest.mock('react-toastify', () => {
    const originalModule = jest.requireActual('react-toastify');
    return {
        __esModule: true,
        ...originalModule,
        toast: (x) => mockToast(x)
    };
});

// Mock the enableEndDateValidation function
jest.mock('main/components/Courses/dateValidation', () => ({
    enableEndDateValidation: jest.fn(),
}));

const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => {
    const originalModule = jest.requireActual('react-router-dom');
    return {
        __esModule: true,
        ...originalModule,
        useParams: () => ({
            id: 17
        }),
        Navigate: (x) => { mockNavigate(x); return null; }
    };
});

describe("CourseJoinPage tests", () => {

    describe("Loading before backend returns", () => {
        const axiosMock = new AxiosMockAdapter(axios);
        const queryClient = new QueryClient();
        beforeEach(() => {
            axiosMock.reset();
            axiosMock.resetHistory();
            axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
            axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
        });
        test("Load data with backend info", async () => {
            render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <CourseJoinPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );
            await screen.findByText("Loading")
        });
    })

    describe("when the backend doesn't return data", () => {

        const axiosMock = new AxiosMockAdapter(axios);

        beforeEach(() => {
            axiosMock.reset();
            axiosMock.resetHistory();
            axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
            axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
            axiosMock.onGet("/api/courses/join", { params: { id: 17 } }).reply(200, coursesFixtures.oneCourse);
        });

        const queryClient = new QueryClient();
        test("Load data with backend info", async () => {
            render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <CourseJoinPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );
            await screen.findByText("Join")
            axiosMock.history.get.forEach(req => console.log(req.url));
            expect(axiosMock.history.get.length).toBeGreaterThanOrEqual(1);
            expect(screen.getByText("Join")).toBeInTheDocument();
            expect(screen.getByTestId("CourseJoin-school")).toBeInTheDocument();
            expect(screen.getByText(/UCSB/)).toBeInTheDocument();
            expect(screen.getByText(/F23/)).toBeInTheDocument();
            expect(screen.getByTestId("CourseJoin-school")).toBeInTheDocument();
            expect(screen.getByTestId("CourseJoin-school")).toHaveTextContent("Course school: UCSB");
        });

        test("Do the job when click join", async () => {
            axiosMock.onPost("/api/courses/join").reply(202, {});
            render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <CourseJoinPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );

            await screen.findByTestId("CourseJoin-school");

            const submitButton = screen.getByText("Join")
            expect(submitButton).toBeInTheDocument();
            expect(submitButton).toHaveStyle("margin-right: 20px")
            fireEvent.click(submitButton);

            await waitFor(() => expect(axiosMock.history.post.length).toBe(1));

            expect(axiosMock.history.post[0].params).toEqual({ courseId: 17 });
            expect(axiosMock.history.post[0].url).toEqual("/api/courses/join");

            await waitFor(() => expect(mockToast).toBeCalled());
            expect(mockToast).toBeCalledWith("Joined successfully");
            expect(mockNavigate).toBeCalledWith({ "to": "/courses" });
            expect(axiosMock.history.post.length).toBe(1); // times called
        });
    });
});

