import React from "react";
import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import { Link } from "react-router-dom"; 


export default function NotFoundPage() {
    return (
        <BasicLayout>
            <div className="text-center pt-5">
                <h1>Oops! We can't find that page.</h1>
                <p>But don't worry, it's probably just lost in space. Let's find you a way back home.</p>
                <div>
                    <input type="text" placeholder="Search for courses." className="m-2" />
                    <button type="submit">Search</button>
                </div>
                <p>Or try these links to get back on track:</p>
                <ul className="list-unstyled">
                  <li><Link to="/">Home</Link></li>
                  <li><Link to="/courses">Courses</Link></li>
                  <li><Link to="/contact">Contact Us</Link></li>
                </ul>
                <p>Did you expect to find something else? <Link to="/feedback">Let us know.</Link></p>
            </div>
        </BasicLayout>
    );
}
