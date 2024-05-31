import StudentsTable from "main/components/Students/StudentsTable";
import { currentUserFixtures } from "fixtures/currentUserFixtures";
import { render, screen } from "@testing-library/react";
import { studentsFixtures } from "fixtures/studentsFixtures";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";

const mockedNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockedNavigate
}));

describe("StudentsTable tests", () => {
    const queryClient = new QueryClient();
    const expectedHeaders = ["id", "courseId", "studentId", "fName", "lName", "email", "githubId"];
    const expectedFields = ["id", "courseId", "studentId", "fName", "lName", "email", "githubId"];
    const testId = "StudentsTable";

    test("Has the expected column headers and content for ordinary user", () => {
        const currentUser = currentUserFixtures.userOnly;

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <StudentsTable students={studentsFixtures.threeStudents} currentUser={currentUser} />
                </MemoryRouter>
            </QueryClientProvider>
        );

        const expectedCourseId = studentsFixtures.threeStudents;
        expectedCourseId.forEach((student, index) => {
            const nameCell = screen.getByTestId(`StudentsTable-cell-row-${index}-col-courseId`);
            expect(nameCell).toHaveTextContent(student.courseId);
        });

        const expectedStudentId = studentsFixtures.threeStudents;
        expectedStudentId.forEach((student, index) => {
            const ageCell = screen.getByTestId(`StudentsTable-cell-row-${index}-col-studentId`);
            expect(ageCell).toHaveTextContent(student.studentId);
        });

        const expectedFName = studentsFixtures.threeStudents;
        expectedFName.forEach((student, index) => {
            const ageCell = screen.getByTestId(`StudentsTable-cell-row-${index}-col-fName`);
            expect(ageCell).toHaveTextContent(student.fName);
        });
        
        const expectedLName = studentsFixtures.threeStudents;
        expectedLName.forEach((student, index) => {
            const ageCell = screen.getByTestId(`StudentsTable-cell-row-${index}-col-lName`);
            expect(ageCell).toHaveTextContent(student.lName);
        });
        
        const expectedEmail = studentsFixtures.threeStudents;
        expectedEmail.forEach((student, index) => {
            const ageCell = screen.getByTestId(`StudentsTable-cell-row-${index}-col-email`);
            expect(ageCell).toHaveTextContent(student.email);
        });
        
        const expectedGithubId = studentsFixtures.threeStudents;
        expectedGithubId.forEach((student, index) => {
            const ageCell = screen.getByTestId(`StudentsTable-cell-row-${index}-col-githubId`);
            expect(ageCell).toHaveTextContent(student.githubId);
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



    test("Renders empty table correctly", () => {
        const currentUser = currentUserFixtures.adminUser;

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <StudentsTable students={[]} currentUser={currentUser} />
                </MemoryRouter>
            </QueryClientProvider>
        );

        expectedHeaders.forEach((headerText) => {
            const header = screen.getByText(headerText);
            expect(header).toBeInTheDocument();
        });

        expectedFields.forEach((field) => {
            const fieldElement = screen.queryByTestId(`${testId}-cell-row-0-col-${field}`);
            expect(fieldElement).not.toBeInTheDocument();
        });
    });

    test("Has the expected column headers and content for adminUser", () => {
        const currentUser = currentUserFixtures.adminUser;

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <StudentsTable students={studentsFixtures.threeStudents} currentUser={currentUser} />
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
    });
});
