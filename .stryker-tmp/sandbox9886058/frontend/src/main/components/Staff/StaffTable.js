import React from "react";
import OurTable from "main/components/OurTable"


 export default function StaffTable({ staff }) {

     // Stryker disable next-line all : TODO try to make a good test for this

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
             Header: 'githubId',
             accessor: 'githubId',
         },
   
     ];

     return <OurTable
         data={staff}
         columns={columns}
         testid={"StaffTable"} />;
    };