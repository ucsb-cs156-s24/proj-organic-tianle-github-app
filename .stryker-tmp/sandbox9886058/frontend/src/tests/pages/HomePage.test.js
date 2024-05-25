import { render, screen } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

import HomePage from "main/pages/HomePage";
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useParams: () => ({
        commonsId: 1
    }),
    useNavigate: () => mockNavigate
}));

describe("HomePage tests", () => {
    const queryClient = new QueryClient();
    const axiosMock = new AxiosMockAdapter(axios);

    beforeEach(() => {
        jest.clearAllMocks();
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    });

    afterEach(() => {
        axiosMock.reset();
      });

    test("expected CSS properties", () => {
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <HomePage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        const title = screen.getByTestId("homePage-title");
        expect(title).toHaveAttribute("style", "font-size: 75px; border-radius: 7px; background-color: white; opacity: 0.9;");
    });

    test('shows greeting for logged-in users with dynamic time of day', () => {
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly); // Adjust this if needed
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <HomePage />
                </MemoryRouter>
            </QueryClientProvider>
        );
    
        const greetingElement = screen.getByTestId("homePage-title");
        expect(
            greetingElement.textContent
        ).toMatch(/Good (morning|afternoon|evening), cgaucho/); 
    });
    
    // test('renders greeting for non-logged-in users correctly', () => {
    //     // Mock the `useCurrentUser` hook to return a logged-out state
    //     jest.mock("main/utils/currentUser", () => ({
    //         useCurrentUser: jest.fn(() => ({ data: { loggedIn: false } }))
    //     }));
    
    //     render(
    //         <QueryClientProvider client={queryClient}>
    //             <MemoryRouter>
    //                 <HomePage />
    //             </MemoryRouter>
    //         </QueryClientProvider>
    //     );
    
    //     const greetingElement = screen.getByTestId("homePage-title");
    //     expect(greetingElement.textContent).toContain("Good afternoon, cgaucho");
    // });  
});

describe('HomePage greetings for not logged in users at different times of the day', () => {
    const originalDate = global.Date;
    const axiosMock = new AxiosMockAdapter(axios);
    const queryClient = new QueryClient();

    const mockDateWithHour = (hour) => {
      global.Date = class extends Date {

        getHours() {
          return hour;
        }
        getDate() { return originalDate.now(); }
        getMonth() { return originalDate.now(); }
        getFullYear() { return originalDate.now(); }
      };
    };
  
    afterEach(() => {
      global.Date = originalDate;
      axiosMock.reset();
        });
    const testCases = [
      { hour: 9, expectedGreeting: "Good morning, cgaucho" },
      { hour: 14, expectedGreeting: "Good afternoon, cgaucho" },
      { hour: 19, expectedGreeting: "Good evening, cgaucho" },
      { hour: 12, expectedGreeting: "Good morning, cgaucho" },
      { hour: 18, expectedGreeting: "Good afternoon, cgaucho" },
    ];
  
    testCases.forEach(({ hour, expectedGreeting }) => {
      test(`shows "${expectedGreeting}" for not logged-in user at ${hour} hours`, () => {
        mockDateWithHour(hour);
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
        render(
          <QueryClientProvider client={queryClient}>
            <MemoryRouter>
              <HomePage />
            </MemoryRouter>
          </QueryClientProvider>
        );
        expect(screen.getByTestId("HomePage-main-div")).toBeInTheDocument();

        const greetingElement = screen.getByTestId("homePage-title");
        expect(greetingElement.textContent).toMatch(new RegExp(expectedGreeting, 'i'));
      });
    });
  });



  describe('HomePage greetings for logged-in users at different times of the day', () => {
    const originalDate = global.Date;
    const axiosMock = new AxiosMockAdapter(axios);
    const queryClient = new QueryClient();


    const mockDateWithHour = (hour) => {
      global.Date = class extends Date {

        getHours() {
          return hour;
        }
        getDate() { return originalDate.now(); }
        getMonth() { return originalDate.now(); }
        getFullYear() { return originalDate.now(); }
      };
    };
  

    beforeEach(() => {
        global.Date = originalDate;
        axiosMock.reset();
        axiosMock.resetHistory();
        //axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.adminUser);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingAll);

    });


    const testCases = [
      { hour: 4, expectedGreeting: "Good morning, pconrad" },
      { hour: 14, expectedGreeting: "Good afternoon, pconrad" },
      { hour: 19, expectedGreeting: "Good evening, pconrad" },
      { hour: 12, expectedGreeting: "Good morning, pconrad" },
      { hour: 18, expectedGreeting: "Good afternoon, pconrad" },
    ];
  
    testCases.forEach(({ hour, expectedGreeting }) =>  {
      test(`shows "${expectedGreeting}" for logged-in user at ${hour} hours`, async () => {
          axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.adminUser);
        mockDateWithHour(hour);
        render(
          <QueryClientProvider client={queryClient}>
            <MemoryRouter>
              <HomePage />
            </MemoryRouter>
          </QueryClientProvider>
        );

        const delay = ms => new Promise(res => setTimeout(res, ms));
        await delay(20);

        expect(screen.getByTestId("HomePage-main-div")).toBeInTheDocument();

        const greetingElement = screen.getByTestId("homePage-title");
        expect(greetingElement.textContent).toMatch(new RegExp(expectedGreeting, 'i'));
      });
    });
  });
   
