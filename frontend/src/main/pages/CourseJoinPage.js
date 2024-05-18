import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import { Navigate } from 'react-router-dom'
import { toast } from "react-toastify";
import { useEffect, useState } from "react";
import { useParams } from 'react-router-dom';
import { Button } from 'react-bootstrap';
import axios from "axios";

export default function CoursesCreatePage({ storybook = false }) {

    let { id } = useParams();
    const [courseInfo, setCourseInfo] = useState(null);
    const [redirectTo, setRedirectTo] = useState(null);

    useEffect(() => {
        // Stryker disable all: this is for storybook
        if (storybook) {
            setCourseInfo({
                "id": 1,
                "name": "Story Test",
                "school": "UCSB",
                "term": "S24",
                "startDate": "1212-12-12T00:12:00",
                "endDate": "1213-12-12T00:12:00",
                "githubOrg": "ucsb-cs-dev"
            })
            return;
        }
        // Stryker restore all
        axios.get(`/api/courses/join?id=${id}`).then((ci) => {
            setCourseInfo(ci.data)
        }).catch(_ => { toast.error("Error loading course info") })
    }, [])

    return (
        <BasicLayout>
            <div className="pt-2">
                <h1>Join Course</h1>
                {!courseInfo && (<h3>Loading</h3>)}
                {
                    courseInfo && (<>
                        <h3>Are you sure you want to join this course?</h3>
                        <p>Course school: {courseInfo.school}</p>
                        <p>Course term: {courseInfo.term}</p>
                        <p>Course name: {courseInfo.name}</p>
                        <Button variant="primary" onClick={() => {
                            axios.post(`/api/courses/join?courseId=${id}`).then(() => {
                                toast.success("Course joined")
                                setRedirectTo("/courses")
                            }).catch(e => toast.error("Error joining course:" + e.response.data.message))
                        }} style={{ marginRight: 20 }}>Join</Button>
                    </>)
                }
                <Button variant="secondary" onClick={() => { setRedirectTo("/courses") }}>Cancel</Button>
                {
                    redirectTo && <Navigate to={redirectTo} />
                }
            </div>
        </BasicLayout>
    )
}