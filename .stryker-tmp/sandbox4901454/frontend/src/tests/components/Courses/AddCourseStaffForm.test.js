import { render, waitFor, fireEvent, screen } from "@testing-library/react";
import AddCourseStaffForm from "main/components/Courses/AddCourseStaffForm";
import { addCourseStaffFixtures } from "fixtures/addCourseStaffFixtures";
import { BrowserRouter as Router } from "react-router-dom";

const mockedNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockedNavigate
}));


describe("AddCourseStaffForm tests", () => {

    test("renders correctly", async () => {

        render(
            <Router  >
                <AddCourseStaffForm />
            </Router>
        );
        await screen.findByText(/courseId/);
        await screen.findByText(/Create/);
    });


    test("renders correctly when passing in a course staff", async () => {

        render(
            <Router  >
                <AddCourseStaffForm initialContents={addCourseStaffFixtures.oneCourseStaff} />
            </Router>
        );
        await screen.findByTestId(/AddCourseStaffForm-id/);
        expect(screen.getByTestId(/AddCourseStaffForm-id/)).toHaveValue("1");
        expect(screen.getByTestId(/AddCourseStaffForm-courseId/)).toHaveValue("12");
        expect(screen.getByTestId(/AddCourseStaffForm-githubId/)).toHaveValue("318493");
    });


    test("Correct Error messsages on missing input", async () => {

        render(
            <Router  >
                <AddCourseStaffForm />
            </Router>
        );
        await screen.findByTestId("AddCourseStaffForm-submit");
        const submitButton = screen.getByTestId("AddCourseStaffForm-submit");

        fireEvent.click(submitButton);

        await screen.findByText(/courseId is required/);
        expect(screen.getByText(/courseId is required/)).toBeInTheDocument();
        expect(screen.getByText(/githubId is required/)).toBeInTheDocument();
    });

    test("No Error messsages on good input", async () => {

        const mockSubmitAction = jest.fn();


        render(
            <Router  >
                <AddCourseStaffForm submitAction={mockSubmitAction} />
            </Router>
        );
        await screen.findByTestId("AddCourseStaffForm-courseId");

        const courseId = screen.getByTestId("AddCourseStaffForm-courseId");
        const githubId = screen.getByTestId("AddCourseStaffForm-githubId");
        const submitButton = screen.getByTestId("AddCourseStaffForm-submit");

        fireEvent.change(courseId, { target: { value: "156" } });
        fireEvent.change(githubId, { target: { value: '1234' } });
        fireEvent.click(submitButton);

        await waitFor(() => expect(mockSubmitAction).toHaveBeenCalled());

        expect(screen.queryByText(/courseId is required./)).not.toBeInTheDocument();
        expect(screen.queryByText(/githubId is required./)).not.toBeInTheDocument();

    });


    test("that navigate(-1) is called when Cancel is clicked", async () => {

        render(
            <Router  >
                <AddCourseStaffForm />
            </Router>
        );
        await screen.findByTestId("AddCourseStaffForm-cancel");
        const cancelButton = screen.getByTestId("AddCourseStaffForm-cancel");

        fireEvent.click(cancelButton);

        await waitFor(() => expect(mockedNavigate).toHaveBeenCalledWith(-1));

    });

});