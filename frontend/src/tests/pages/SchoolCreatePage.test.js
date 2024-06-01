import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import SchoolCreatePage from "main/pages/SchoolCreatePage";

import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";

import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

const mockToast = jest.fn();
jest.mock("react-toastify", () => {
  const originalModule = jest.requireActual("react-toastify");
  return {
    __esModule: true,
    ...originalModule,
    toast: (x) => mockToast(x),
  };
});

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => {
  const originalModule = jest.requireActual("react-router-dom");
  return {
    __esModule: true,
    ...originalModule,
    Navigate: (x) => {
      mockNavigate(x);
      return null;
    },
  };
});

describe("SchoolCreatePage tests", () => {
  const axiosMock = new AxiosMockAdapter(axios);

  beforeEach(() => {
    jest.clearAllMocks();
    axiosMock.reset();
    axiosMock.resetHistory();
    axiosMock
      .onGet("/api/currentUser")
      .reply(200, apiCurrentUserFixtures.userOnly);
    axiosMock
      .onGet("/api/systemInfo")
      .reply(200, systemInfoFixtures.showingNeither);
  });

  const queryClient = new QueryClient();
  test("renders without crashing", () => {
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <SchoolCreatePage />
        </MemoryRouter>
      </QueryClientProvider>
    );
  });

  test("on submit, makes request to backend", async () => {
    const queryClient = new QueryClient();
    const school = {
      abbrev: "ucsb",
      name: "University of California, Santa Barbara",
      termRegex: "[WSMF]\\d\\d",
      termDescription: "S24",
      termError: "error",
    };

    axiosMock.onPost("/api/schools/post").reply(202, school);

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <SchoolCreatePage />
        </MemoryRouter>
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId("SchoolForm-abbrev")).toBeInTheDocument();
    });

    const abbrevField = screen.getByTestId("SchoolForm-abbrev");
    const nameField = screen.getByTestId("SchoolForm-name");
    const termRegexField = screen.getByTestId("SchoolForm-termRegex");
    const termDescriptionField = screen.getByTestId("SchoolForm-termDescription");
    const termErrorField = screen.getByTestId("SchoolForm-termError");
    const submitButton = screen.getByTestId("SchoolForm-submit");

    fireEvent.change(abbrevField, { target: { value: "ucsb" } });
    fireEvent.change(nameField, {target: { value: "University of California, Santa Barbara" }});
    fireEvent.change(termRegexField, { target: { value: "[WSMF]\\d\\d" } });
    fireEvent.change(termDescriptionField, { target: { value: "S24" } });
    fireEvent.change(termErrorField, { target: { value: "error" } });
    expect(submitButton).toBeInTheDocument();

    fireEvent.click(submitButton);

    await waitFor(() => expect(axiosMock.history.post.length).toBe(1));

    expect(axiosMock.history.post[0].params).toEqual({
      abbrev: "ucsb",
      name: "University of California, Santa Barbara",
      termRegex: "%5BWSMF%5D%5Cd%5Cd", // termRegex is taken in encoded but it is decoded when actually used in the api
      termDescription: "S24",
      termError: "error",
    });

    expect(mockToast).toBeCalledWith(`New school created - abbrev: ucsb`);
    expect(mockNavigate).toBeCalledWith({ to: "/admin/schools" });
  });
});
