import React from 'react'
import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import { useParams } from "react-router-dom";
import StaffTable from 'main/components/Staff/StaffTable';
import { useBackend } from "main/utils/useBackend";
import { Button } from 'react-bootstrap';
import { useCurrentUser, hasRole} from 'main/utils/currentUser';


export default function CoursesStaffPage() {
  let { courseId } = useParams();
  const { data: currentUser } = useCurrentUser();
  const createButton = () => {  
      
    return (
        <Button
        variant="primary"
        href={`/courses/${courseId}/staff/addStaff`}
        style={{ float: "right" }}
        >
            Add Staff
        </Button>
    )
  
  }
  const { data: staff, error: _error, status: _status } =
    useBackend(
      // Stryker disable next-line all : don't test internal caching of React Query
      [`/api/courses/getStaff?courseId=${courseId}`],
      {  // Stryker disable next-line all : GET is the default
        method: "GET", 
        url: `/api/courses/getStaff`,
        params:{courseId}
        
        
      },[]
    
      
    );
    
    

    return (
      <BasicLayout>
        <div className="pt-2">
        {(hasRole(currentUser, "ROLE_ADMIN") || hasRole(currentUser, "ROLE_INSTRUCTOR")) && createButton()}
          <h1>Staff</h1>
          <StaffTable staff={staff} />
    
        </div>
      </BasicLayout>
    )
}
