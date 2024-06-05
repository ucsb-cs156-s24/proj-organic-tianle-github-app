import { render, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";
import CoursesShowPage from "main/pages/CoursesShowPage";

import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";

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

describe("CoursesShowPage tests", () => {

  describe("when the backend doesn't return data", () => {

      const axiosMock = new AxiosMockAdapter(axios);

      beforeEach(() => {
          axiosMock.reset();
          axiosMock.resetHistory();
          axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
          axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
          axiosMock.onGet("/api/courses/get", { params: { id: 17 } }).timeout();
      });

      const queryClient = new QueryClient();
      test("renders header but table is not present", async () => {

          const restoreConsole = mockConsole();

          render(
              <QueryClientProvider client={queryClient}>
                  <MemoryRouter>
                      <CoursesShowPage />
                  </MemoryRouter>
              </QueryClientProvider>
          );
          await screen.findByText("Course 17 Info");
          expect(screen.queryByTestId("CoursesForm-name")).not.toBeInTheDocument();
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
          axiosMock.onGet("/api/courses/get", { params: { id: 17 } }).reply(200, {
              id: 17,
              name: "CS 156",
              school: "UCSB",
              term: "f23",
              startDate: "2023-09-29T00:00",
              endDate: "2023-12-15T00:00",
              githubOrg: "ucsb-cs156-f23"
          });
      });

      const queryClient = new QueryClient();
      test("renders without crashing", () => {
          render(
              <QueryClientProvider client={queryClient}>
                  <MemoryRouter>
                      <CoursesShowPage />
                  </MemoryRouter>
              </QueryClientProvider>
          );
      });

      test("renders with data", () => {
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <CoursesShowPage />
                </MemoryRouter>
            </QueryClientProvider>
        );
        expect(screen.getByText("CS 156")).toBeInTheDocument();
        expect(screen.getByText("UCSB")).toBeInTheDocument();
        expect(screen.getByText("f23")).toBeInTheDocument();
    });

  });
});