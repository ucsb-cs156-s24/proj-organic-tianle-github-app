import React from 'react'
import { useBackend } from 'main/utils/useBackend';
import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import CoursesTable from 'main/components/Courses/CoursesTable';
import { Button } from 'react-bootstrap';
import { useCurrentUser, hasRole} from 'main/utils/currentUser';

export default function CourseShowPage() {

  const { id } = useParams();
  const { data: currentUser } = useCurrentUser();

  const showButton = () => {
    return (
      <Button
        variant="primary"
        href={`/courses/${id}`} // Use the course id in the URL
        style={{ float: "right" }}
      >
        Show Course 
      </Button>
    )
  }
  
  const { data: courses, error: _error, status: _status } =
    useBackend(
      // Stryker disable next-line all : don't test internal caching of React Query
      [`/api/courses?id=${id}`],
      // Stryker disable next-line all : GET is the default
      { method: "GET", url: "/api/courses/all", params: { id }},
      []
    );

    return (
      <BasicLayout>
        <div className="pt-2">
          {(hasRole(currentUser, "ROLE_ADMIN") || hasRole(currentUser, "ROLE_INSTRUCTOR")) && showButton()}
          <h1>Show Course</h1>
          <CoursesTable courses={courses} currentUser={currentUser} />
    
        </div>
      </BasicLayout>
    )
}
