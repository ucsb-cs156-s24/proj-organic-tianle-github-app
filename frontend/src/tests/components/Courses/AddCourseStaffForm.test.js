import { render, waitFor, fireEvent, screen } from "@testing-library/react";
import AddCourseStaffForm from "main/components/Courses/AddCourseStaffForm";
import { staffFixture } from "fixtures/staffFixture";
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
        await screen.findByText(/Create/);
    });


    test("renders correctly when passing in a course staff", async () => {

        render(
            <Router  >
                <AddCourseStaffForm initialContents={staffFixture.oneStaff} />
            </Router>
        );
        await screen.findByTestId(/AddCourseStaffForm-id/);
        expect(screen.getByTestId(/AddCourseStaffForm-id/)).toHaveValue("1");
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

        await screen.findByText(/githubId is required/);
        expect(screen.getByText(/githubId is required/)).toBeInTheDocument();
    });

    test("No Error messsages on good input", async () => {

        const mockSubmitAction = jest.fn();


        render(
            <Router  >
                <AddCourseStaffForm submitAction={mockSubmitAction} />
            </Router>
        );
        await screen.findByTestId("AddCourseStaffForm-githubId");

        const githubId = screen.getByTestId("AddCourseStaffForm-githubId");
        const submitButton = screen.getByTestId("AddCourseStaffForm-submit");

        
        fireEvent.change(githubId, { target: { value: 'scottpchow23' } });
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