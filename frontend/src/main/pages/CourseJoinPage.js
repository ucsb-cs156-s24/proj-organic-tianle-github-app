import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import { Navigate } from 'react-router-dom'
import { toast } from "react-toastify";
import { useParams } from 'react-router-dom';
import { Button } from 'react-bootstrap';
import { useBackend, useBackendMutation } from "main/utils/useBackend";

export default function CoursesCreatePage({ storybook = false }) {

    let { id } = useParams();


    const { data: courseInfo, __error, __status } =
        useBackend(
            // Stryker disable next-line all : don't test internal caching of React Query
            [`/api/courses/join?id=${id}`],
            {  // Stryker disable next-line all : GET is the default, so changing this to "" doesn't introduce a bug
                method: "GET",
                url: `/api/courses/join`,
                params: {
                    id
                }
            }
        );

    const objectToAxiosPutParams = (_course) => ({
        url: "/api/courses/join",
        method: "POST",
        params: {
            courseId: id
        },
    });

    const onSuccess = (_course) => {
        toast(`Joined successfully`);
    }

    const mutation = useBackendMutation(
        objectToAxiosPutParams,
        { onSuccess },
        // Stryker disable next-line all : hard to set up test for caching
        [`/api/courses/join?courseId=${id}`]
    );

    const { isSuccess } = mutation

    const onSubmit = async (_data) => {
        mutation.mutate({});
    }

    if (isSuccess && !storybook) {
        return <Navigate to="/courses" />
    }

    return (
        <BasicLayout>
            <div className="pt-2">
                <h1>Join Course</h1>
                {!courseInfo && (<h3>Loading</h3>)}
                {
                    courseInfo && (<>
                        <h3>Are you sure you want to join this course?</h3>
                        <p data-testid="CourseJoin-school">Course school: {courseInfo.school}</p>
                        <p>Course term: {courseInfo.term}</p>
                        <p>Course name: {courseInfo.name}</p>
                        <Button variant="primary" onClick={onSubmit} style={{ marginRight: 20 }}>Join</Button>
                    </>)
                }
                {/* <Button variant="secondary" onClick={() => { setRedirectTo("/courses") }}>Cancel</Button> */}
            </div>
        </BasicLayout>
    )
}