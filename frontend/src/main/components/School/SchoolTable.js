import React from "react";
 import OurTable, { ButtonColumn } from "main/components/OurTable"
 import { useBackendMutation } from "main/utils/useBackend";
 import { cellToAxiosParamsDelete, onDeleteSuccess } from "main/components/Utils/SchoolUtils" 
 import { useNavigate } from "react-router-dom";
 import { hasRole } from "main/utils/currentUser";


  //FIXME: SchoolTable or should it be plural? Everything is singular except reference to the api fyi

 export default function SchoolTable({ school, currentUser }) {

     const navigate = useNavigate();

     const editCallback = (cell) => {
         navigate(`/admin/schools/edit/${cell.row.values.abbrev}`);
     };

     // Stryker disable all : hard to test for query caching

     const deleteMutation = useBackendMutation(
         cellToAxiosParamsDelete,
         { onSuccess: onDeleteSuccess },
         ["/api/schools/all"]
     );
     // Stryker restore all 

     // Stryker disable next-line all : TODO try to make a good test for this
     const deleteCallback = async (cell) => { deleteMutation.mutate(cell); }

     const columns = [
         {
             Header: 'Abbrev',
             accessor: 'abbrev',
         },
         {
             Header: 'Name',
             accessor: 'name',
         },
         {
             Header: 'TermRegex',
             accessor: 'termRegex',
         },
         {
             Header: 'TermDescription',
             accessor: 'termDescription',
         },
         {
             Header: 'TermError',
             accessor: 'termError',
         },
     ];

     if (hasRole(currentUser, "ROLE_ADMIN")) {
         columns.push(ButtonColumn("Edit", "primary", editCallback, "SchoolTable"));
         columns.push(ButtonColumn("Delete", "danger", deleteCallback, "SchoolTable"));
     }

     return <OurTable
         data={school}
         columns={columns}
         testid={"SchoolTable"} />;
    };