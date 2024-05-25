import { render, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

import NotFoundPage from "main/pages/NotFoundPage";
import { apiCurrentUserFixtures }  from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";

describe("NotFoundPage tests", () => {
    const queryClient = new QueryClient();
    const axiosMock = new AxiosMockAdapter(axios);

    beforeEach(()=>{
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
    });

    test("renders correctly without crashing", async () => {
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <NotFoundPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        expect(
            screen.getByText("Oops! We can't find that page.")
          ).toBeInTheDocument();
          expect(
            screen.getByText(
              "But don't worry, it's probably just lost in space. Let's find you a way back home."
            )
          ).toBeInTheDocument();
          expect(screen.getByPlaceholderText("Search for courses.")).toBeInTheDocument();
          expect(screen.getByRole("button", { name: "Search" })).toBeInTheDocument();
          expect(screen.getByText("Home")).toBeInTheDocument();
          expect(screen.getByText("Courses")).toBeInTheDocument();
          expect(screen.getByText("Contact Us")).toBeInTheDocument();
          expect(
            screen.getByText("Did you expect to find something else?")
          ).toBeInTheDocument();
          expect(
            screen.getByRole("link", { name: "Let us know." })
          ).toBeInTheDocument();
         });

});