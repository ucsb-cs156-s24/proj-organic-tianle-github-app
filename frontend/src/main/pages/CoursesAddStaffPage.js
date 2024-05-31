import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import AddCourseStaffForm from "main/components/Courses/AddCourseStaffForm";
import { Navigate } from 'react-router-dom'
import { useBackendMutation } from "main/utils/useBackend";
import { toast } from "react-toastify";
import { useParams } from "react-router-dom";

export default function AddCourseStaffPage({storybook=false}) {
    let { courseId } = useParams();
    const objectToAxiosParams = (staff) => ({
        url: "/api/courses/addStaff",
        method: "POST",
        params: {
            courseId: staff.courseId,
            githubLogin: staff.githubId
        }
    });

    const onSuccess = (staff) => {
        toast(`New staff added - courseId: ${staff.courseId}`);
    }

    const mutation = useBackendMutation(
        objectToAxiosParams,
        { onSuccess }, 
        // Stryker disable next-line all : hard to set up test for caching
        [`/api/courses/getStaff?courseId=${courseId}`] // mutation makes this key stale so that pages relying on it reload
        );

    const { isSuccess } = mutation

    const onSubmit = async (data) => {
        mutation.mutate(data);
    }
    
    if (isSuccess && !storybook) {
        return <Navigate to={`/courses/${courseId}/staff`} />
    }

    return (
        <BasicLayout>
        <div className="pt-2">
            <h1>Add New Staff Member</h1>

            <AddCourseStaffForm submitAction={onSubmit} />

        </div>
        </BasicLayout>
    )
}