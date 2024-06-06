import React from 'react'
import { useBackend } from 'main/utils/useBackend';

import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import SchoolTable from 'main/components/School/SchoolTable';
import { Button } from 'react-bootstrap';
import { useCurrentUser} from 'main/utils/currentUser';

export default function SchoolIndexPage() {

    const { data: currentUser } = useCurrentUser();
  

    const createButton = () => {  
      
        return (
            <Button
                variant="primary"
                href="/admin/schools/create"
                style={{ float: "right" }}
            >
                Create School 
            </Button>
        )
      
    }
    
    const { data: school, error: _error, status: _status } =
      useBackend(
        // Stryker disable next-line all : don't test internal caching of React Query
        ["/api/schools/all"],
        // Stryker disable next-line all : GET is the default
        { method: "GET", url: "/api/schools/all" },
        []
      );
  
      return (
        <BasicLayout>
          <div className="pt-2">
            {createButton()}
            <h1>School</h1>
            <SchoolTable school={school} currentUser={currentUser} />
          </div>
        </BasicLayout>
      )
  }