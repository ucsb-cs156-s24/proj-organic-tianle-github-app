import StaffTable from "main/components/Staff/StaffTable"
import { currentUserFixtures } from "fixtures/currentUserFixtures";
// import { fireEvent, render, waitFor, screen } from "@testing-library/react";
import {  render, screen } from "@testing-library/react";
import { staffFixture } from "fixtures/staffFixture";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";


const mockedNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockedNavigate
}));

describe("StaffTable tests", () => {
  const queryClient = new QueryClient();
  const expectedHeaders = ["id", "courseId", "githubId"];
  const expectedFields = ["id", "courseId", "githubId"]; 
  const testId = "StaffTable";

  test("Has the expected column headers and content for ordinary user", () => {

    const currentUser = currentUserFixtures.userOnly;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <StaffTable staff={staffFixture.threeStaff} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );

    const expectedStaffgithub = staffFixture.threeStaff;
    expectedStaffgithub.forEach((staffMember, index) => {
      const githubIdCell = screen.getByTestId(`StaffTable-cell-row-${index}-col-githubId`);
      expect(githubIdCell).toHaveTextContent(staffMember.githubId);
    });

    const expectedStaffcourse = staffFixture.threeStaff;
    expectedStaffcourse.forEach((staffMember, index) => {
      const courseIdCell = screen.getByTestId(`StaffTable-cell-row-${index}-col-courseId`);
      expect(courseIdCell).toHaveTextContent(staffMember.courseId);
    });

    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    expectedFields.forEach((field) => {
      const header = screen.getByTestId(`${testId}-cell-row-0-col-${field}`);
      expect(header).toBeInTheDocument();
    });

    expect(screen.getByTestId(`${testId}-cell-row-0-col-id`)).toHaveTextContent("1");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-id`)).toHaveTextContent("2");


    
    const editButton = screen.queryByTestId(`${testId}-cell-row-0-col-Edit-button`);
    expect(editButton).not.toBeInTheDocument();

    const deleteButton = screen.queryByTestId(`${testId}-cell-row-0-col-Delete-button`);
    expect(deleteButton).not.toBeInTheDocument();

  });

  test("renders empty table correctly", () => {

    // arrange
    const currentUser = currentUserFixtures.adminUser;


    // act
    render(
        <QueryClientProvider client={queryClient}>
          <MemoryRouter>
          <StaffTable staff={[]} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>
    );



    // assert
    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    expectedFields.forEach((field) => {
      const fieldElement = screen.queryByTestId(`${testId}-cell-row-0-col-${field}`);
      expect(fieldElement).not.toBeInTheDocument();
    });
  });


  test("Has the expected colum headers and content for adminUser", () => {

    const currentUser = currentUserFixtures.adminUser;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
            <StaffTable staff={staffFixture.threeStaff} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );


    expectedHeaders.forEach((headerText) => {
      const header = screen.getByText(headerText);
      expect(header).toBeInTheDocument();
    });

    expectedFields.forEach((field) => {
      const header = screen.getByTestId(`${testId}-cell-row-0-col-${field}`);
      expect(header).toBeInTheDocument();
    });

    expect(screen.getByTestId(`${testId}-cell-row-0-col-id`)).toHaveTextContent("1");
    expect(screen.getByTestId(`${testId}-cell-row-1-col-id`)).toHaveTextContent("2");

    // const deleteButton = screen.getByTestId(`${testId}-cell-row-0-col-Delete-button`);
    // expect(deleteButton).toBeInTheDocument();
    // expect(deleteButton).toHaveClass("btn-danger");

  });




  

});