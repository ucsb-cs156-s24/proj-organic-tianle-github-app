import React from "react";
 import OurTable from "main/components/OurTable"

 export default function  StudentsTable({ students }) {
     const columns = [
         {
             Header: 'id',
             accessor: 'id',
         },
         {
             Header: 'courseId',
             accessor: 'courseId',
         },
         {
            Header: 'studentId',
            accessor: 'studentId',
        },
        {
            Header: 'fName',
            accessor: 'fName',
        },
        {
            Header: 'lName',
            accessor: 'lName',
        },
        {
            Header: 'email',
            accessor: 'email',
        },
        {
            Header: 'githubId',
            accessor: 'githubId',
        },

  
     ];

     return (
        <>
            <div>Total Students: {students.length}</div>
          <OurTable data={students} columns={columns} testid={"StudentsTable"} />
        </>
      );
    };