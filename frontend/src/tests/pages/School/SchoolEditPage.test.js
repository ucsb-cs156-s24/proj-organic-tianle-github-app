import { fireEvent, render, waitFor, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import SchoolEditPage from "main/pages/SchoolEditPage";

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
        toast: (x) => mockToast(x)
    };
});

const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => {
    const originalModule = jest.requireActual('react-router-dom');
    return {
        __esModule: true,
        ...originalModule,
        useParams: () => ({
            abbrev: "ucsb"
        }),
        Navigate: (x) => { mockNavigate(x); return null; }
    };
});


describe("SchoolEditPage tests", () => {

    describe("when the backend doesn't return data", () => {

        const axiosMock = new AxiosMockAdapter(axios);

        beforeEach(() => {
            axiosMock.reset();
            axiosMock.resetHistory();
            axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
            axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
            axiosMock.onGet("/api/schools", { params: { abbrev: "ucsb" } }).timeout();
        });

        const queryClient = new QueryClient();
        test("renders header but table is not present", async () => {

            const restoreConsole = mockConsole();

            render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <SchoolEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );
            await screen.findByText("Edit School");
            expect(screen.queryByTestId("SchoolsForm-name")).not.toBeInTheDocument();
            restoreConsole();
        });
    });

    describe("tests where backend is working normally", () => {

        const axiosMock = new AxiosMockAdapter(axios);

        beforeEach(() => {
            axiosMock.reset();
            axiosMock.resetHistory();
            axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
            axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
            axiosMock.onGet("/api/schools", { params: { abbrev: "ucsb" } }).reply(200, {
                abbrev: "ucsb",
                name: "University of California, Santa Barbara",
                termRegex: "regexTest",
                termDescription: "descriptionTest",
            });
            axiosMock.onPut('/api/schools/update').reply(200, {
                abbrev: "ucsb",
                name: "University of California, Sha Bi",
                termRegex: "regexTest2",
                termDescription: "descText2",
            });
        });

        const queryClient = new QueryClient();
        test("renders without crashing", () => {
            render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <SchoolEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );
        });

        test("Is populated with the data provided", async () => {

            render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <SchoolEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );

            await screen.findByTestId("SchoolForm-abbrev");

            const nameField = screen.getByTestId("SchoolForm-name");
            const abbrevField = screen.getByTestId("SchoolForm-abbrev");
            const termRegexField = screen.getByTestId("SchoolForm-termRegex");
            const termDescriptionField = screen.getByTestId("SchoolForm-termDescription");
            const submitButton = screen.getByTestId("SchoolForm-submit");

            expect(nameField).toHaveValue("University of California, Santa Barbara");
            expect(abbrevField).toHaveValue("ucsb");
            expect(termRegexField).toHaveValue("regexTest");
            expect(termDescriptionField).toHaveValue("descriptionTest");
            expect(submitButton).toBeInTheDocument();
        });

        test("Changes when you click Update", async () => {

            render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <SchoolEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );

            await screen.findByTestId("SchoolForm-abbrev");

            const nameField = screen.getByTestId("SchoolForm-name");
            const abbrevField = screen.getByTestId("SchoolForm-abbrev");
            const termRegexField = screen.getByTestId("SchoolForm-termRegex");
            const termDescriptionField = screen.getByTestId("SchoolForm-termDescription");
            const submitButton = screen.getByTestId("SchoolForm-submit");

            expect(nameField).toHaveValue("University of California, Santa Barbara");
            expect(abbrevField).toHaveValue("ucsb");
            expect(termRegexField).toHaveValue("regexTest");
            expect(termDescriptionField).toHaveValue("descriptionTest");
            expect(submitButton).toBeInTheDocument();

            fireEvent.change(nameField, { target: { value: "University of California, Sha Bi" } })
            fireEvent.change(termRegexField, { target: { value: "regexTest2" } })
            fireEvent.change(termDescriptionField, { target: { value: "descTest2" } })

            fireEvent.click(submitButton);

            await waitFor(() => expect(mockToast).toBeCalled());
            expect(mockToast).toBeCalledWith("School Updated - abbrev: ucsb name: University of California, Sha Bi");
            expect(mockNavigate).toBeCalledWith({ "to": "/admin/schools" });

            expect(axiosMock.history.put.length).toBe(1);
            expect(axiosMock.history.put[0].params).toEqual({ abbrev: "ucsb"});
            expect(axiosMock.history.put[0].data).toEqual(JSON.stringify({ name: "University of California, Sha Bi", termRegex: "regexTest2", termDescription: "descTest2"}));

        });

       
    });
});

