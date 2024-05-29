import React from "react";
import OurTable, { ButtonColumn } from "main/components/OurTable"
import { useBackendMutation } from "main/utils/useBackend";
import { cellToAxiosParamsDelete, onDeleteSuccess } from "main/components/Utils/CoursesUtils"
import { useNavigate } from "react-router-dom";
import { hasRole } from "main/utils/currentUser";

export default function CoursesTable({ courses, currentUser }) {

    const navigate = useNavigate();

    const joinCallback = (cell) => {
        navigate(`/courses/join/${cell.row.values.id}`);
    };

    const staffCallback = (cell) => {
        navigate(`/courses/${cell.row.values.id}/staff`);
    };

    const editCallback = (cell) => {
        navigate(`/courses/edit/${cell.row.values.id}`);
    };

    // Stryker disable all : hard to test for query caching

    const deleteMutation = useBackendMutation(
        cellToAxiosParamsDelete,
        { onSuccess: onDeleteSuccess },
        ["/api/courses/all"]
    );
    // Stryker restore all 

    // Stryker disable next-line all : TODO try to make a good test for this
    const deleteCallback = async (cell) => { deleteMutation.mutate(cell); }

    const columns = [
        {
            Header: 'id',
            accessor: 'id',
        },
        {
            Header: 'Name',
            accessor: 'name',
        },
        {
            Header: 'School',
            accessor: 'school',
        },
        {
            Header: 'Term',
            accessor: 'term',
        },
        {
            Header: 'StartDate',
            accessor: 'startDate',
        },
        {
            Header: 'EndDate',
            accessor: 'endDate',
        },
        {
            Header: 'GitHub Org',
            accessor: 'githubOrg',
        },

    ];

    if (hasRole(currentUser, "ROLE_ADMIN") || hasRole(currentUser, "ROLE_INSTRUCTOR")) {
        columns.push({
            Header: 'GitHub App Installed',
            accessor: 'githubAppInstalled',
        })
        columns.push(ButtonColumn("Staff", "primary", staffCallback, "CoursesTable"));
        columns.push(ButtonColumn("Edit", "primary", editCallback, "CoursesTable"));
        columns.push(ButtonColumn("Delete", "danger", deleteCallback, "CoursesTable"));
    }

    columns.push(ButtonColumn("Join", "primary", joinCallback, "CoursesTable"));

    const coursesMapped = courses.map((course) => {
        return {
            ...course,
            githubAppInstalled: course.githubAppInstallationId ? "✔️" : "⚠️",
        };
    })

    return (
        <>
            <div>Total Courses: {coursesMapped.length}</div>
            <OurTable data={coursesMapped} columns={columns} testid={"CoursesTable"} />
            <p>If you see a ⚠️ next to the github org label, it means you need to install the github app in your course organization. Click the edit button to do that.</p>
        </>
    );
};