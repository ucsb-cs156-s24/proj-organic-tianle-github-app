import React from "react";
import { useCurrentUser } from "main/utils/currentUser"; 
import BasicLayout from "main/layouts/BasicLayout/BasicLayout";


export default function HomePage() {

    const { data: currentUser } = useCurrentUser();

    const getPartOfDayGreeting = () => {
        const hour = new Date().getHours();
        if (hour <= 12) return "Good morning";
        if (hour <= 18) return "Good afternoon";
        return "Good evening";
    };

    const greeting = getPartOfDayGreeting();

    if (!currentUser || !currentUser.loggedIn) {
        return (
            <div data-testid={"HomePage-main-div"}>
                <BasicLayout>
                    <h1 data-testid="homePage-title" style={{ fontSize: "75px", borderRadius: "7px", backgroundColor: "white", opacity: ".9" }} className="text-center border-0 my-3">
                        {greeting}, cgaucho
                    </h1>
                </BasicLayout>
            </div>
        );
    }

    // Stryker disable all : TODO: restructure this code to avoid the need for this disable
    return (
        <div data-testid={"HomePage-main-div"}>
            <BasicLayout>
                <h1 data-testid="homePage-title" style={{ fontSize: "75px", borderRadius: "7px", backgroundColor: "white", opacity: ".9" }} className="text-center border-0 my-3">
                    {greeting}, {currentUser.root.user.githubLogin}
                </h1>
            </BasicLayout>
        </div>
    );
}
