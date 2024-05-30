const schoolsFixtures = {
    oneSchool: {
        "abbrev": "ucsb",
        "name": "UC Santa Barbara",
        "termRegex": "[WSMF]\\d\\d",
        "termDescription": "Enter quarter, e.g. F23, W24, S24, M24"
    },
    threeSchools: [
        { 
            "abbrev": "ucsb",
            "name": "UC Santa Barbara",
            "termRegex": "[WSMF]\\d\\d",
            "termDescription": "Enter quarter, e.g. F23, W24, S24, M24"
        },
        {
            "abbrev": "umn",
            "name": "University of Minnesota",
            "termRegex": "[WSMF]\\d\\d",
            "termDescription": "Enter quarter, e.g. F23, W24, S24, M24"
        },
        {
            "abbrev": "ucsd",
            "name": "UC San Diego",
            "termRegex": "[WSMF]\\d\\d",
            "termDescription": "Enter quarter, e.g. F23, W24, S24, M24"
        }
    ]
};


export { schoolsFixtures };