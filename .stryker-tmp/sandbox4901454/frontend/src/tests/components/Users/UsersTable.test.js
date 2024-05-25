

import{ render, screen, fireEvent }  from "@testing-library/react";
import UsersTable from "main/components/Users/UsersTable";
import { formatTime } from "main/utils/dateUtils";
import usersFixtures from "fixtures/usersFixtures";
import { QueryClient, QueryClientProvider } from "react-query";

describe("UserTable tests", () => {
    const queryClient = new QueryClient();
    const testId = "UsersTable";

    beforeEach(() => {
        // Mock window.confirm
        window.confirm = jest.fn();

      });

    test("renders without crashing for empty table", () => {
        render(
            <QueryClientProvider client={queryClient}>
                <UsersTable users={[]} />
            </QueryClientProvider>
        );
    });

    test("renders without crashing for three users", () => {
        render(
            <QueryClientProvider client={queryClient}>
                <UsersTable users={usersFixtures.threeUsers} />
            </QueryClientProvider>
        ); 
    });

    test("Has the expected column headers and content as admin user", () => {
        render(
            <QueryClientProvider client={queryClient}>
                <UsersTable users={usersFixtures.threeUsers} showToggleButtons={true} />
            </QueryClientProvider>
        );
    
        const expectedHeaders = ["githubId", "githubLogin", "fullName", "Email", "Last Online", "Admin", "Instructor"];
        const expectedFields = ["githubId", "githubLogin", "fullName", "email", "lastOnline", "admin", "instructor"];
        const testId = "UsersTable";

        expectedHeaders.forEach( (headerText)=> {
            const header = screen.getByText(headerText);
            expect(header).toBeInTheDocument();
        });

        expectedFields.forEach( (field)=> {
          const header = screen.getByTestId(`${testId}-cell-row-0-col-${field}`);
          expect(header).toBeInTheDocument();
        });

        expect(screen.getByTestId(`${testId}-cell-row-0-col-toggle-admin-button`)).toBeInTheDocument();
        expect(screen.getByTestId(`${testId}-cell-row-0-col-toggle-admin-button`)).toHaveClass("btn-primary");
        expect(screen.getByTestId(`${testId}-cell-row-0-col-toggle-instructor-button`)).toBeInTheDocument();
        expect(screen.getByTestId(`${testId}-cell-row-0-col-toggle-instructor-button`)).toHaveClass("btn-primary");

        expect(screen.getByTestId(`${testId}-cell-row-0-col-githubId`)).toHaveTextContent("11111");
        expect(screen.getByTestId(`${testId}-cell-row-0-col-admin`)).toHaveTextContent("true");
        expect(screen.getByTestId(`${testId}-cell-row-0-col-fullName`)).toHaveTextContent("Phill Conrad");
        expect(screen.getByTestId(`${testId}-cell-row-0-col-lastOnline`)).toHaveTextContent(formatTime(usersFixtures.threeUsers[0].lastOnline));
        expect(screen.getByTestId(`${testId}-cell-row-1-col-githubLogin`)).toHaveTextContent("cgaucho");
        expect(screen.getByTestId(`${testId}-cell-row-1-col-admin`)).toHaveTextContent("false");
        expect(screen.getByTestId(`${testId}-cell-row-1-col-instructor`)).toHaveTextContent("true");
      });

      test("Does not see toggle admin and instructor buttons as regular user", () => {
        render(
            <QueryClientProvider client={queryClient}>
                <UsersTable users={usersFixtures.threeUsers}/>
            </QueryClientProvider>
        );
    
        const expectedHeaders = ["githubId", "githubLogin", "fullName", "Email", "Last Online", "Admin", "Instructor"];
        const expectedFields = ["githubId", "githubLogin", "fullName", "email", "lastOnline", "admin", "instructor"];
        const testId = "UsersTable";

        expectedHeaders.forEach( (headerText)=> {
            const header = screen.getByText(headerText);
            expect(header).toBeInTheDocument();
        });

        expectedFields.forEach( (field)=> {
          const header = screen.getByTestId(`${testId}-cell-row-0-col-${field}`);
          expect(header).toBeInTheDocument();
        });

        expect(screen.queryByText('toggle-admin')).not.toBeInTheDocument();
        expect(screen.queryByText('toggle-instructor')).not.toBeInTheDocument();
      });

      test("renders toggle buttons when showToggleButtons is true", () => {
        render(
            <QueryClientProvider client={queryClient}>
                <UsersTable users={usersFixtures.threeUsers} showToggleButtons={true} />
            </QueryClientProvider>
        );

        const toggleAdminButton = screen.getByTestId(`${testId}-cell-row-0-col-toggle-admin-button`);
        expect(toggleAdminButton).toBeInTheDocument();
        const toggleInstructorButton = screen.getByTestId(`${testId}-cell-row-0-col-toggle-instructor-button`);
        expect(toggleInstructorButton).toBeInTheDocument();

        fireEvent.click(toggleInstructorButton);
        fireEvent.click(toggleAdminButton);
    });

      test("confirms toggle admin action when dialog accepted", async () => {
        render(
            <QueryClientProvider client={queryClient}>
                <UsersTable users={usersFixtures.threeUsers} showToggleButtons={true}/>
            </QueryClientProvider>
        );

        const testId = "UsersTable";
        const toggleAdminButton = screen.getByTestId(`${testId}-cell-row-0-col-toggle-admin-button`);
        fireEvent.click(toggleAdminButton);

        expect(window.confirm).toHaveBeenCalledWith("Are you sure you want to toggle the admin status for this user?");

    });

    test("confirms toggle instructor action when dialog accepted", async () => {
        render(
            <QueryClientProvider client={queryClient}>
                <UsersTable users={usersFixtures.threeUsers} showToggleButtons={true}/>
            </QueryClientProvider>
        );

        const testId = "UsersTable";
        const toggleinstructorButton = screen.getByTestId(`${testId}-cell-row-0-col-toggle-instructor-button`);
        fireEvent.click(toggleinstructorButton);

        expect(window.confirm).toHaveBeenCalledWith("Are you sure you want to toggle the instructor status for this user?");

    });

});

