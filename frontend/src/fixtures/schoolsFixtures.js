const schoolsFixtures = {
    oneSchool: {
        "abbrev": "ucsb",
        "name": "UC Santa Barbara",
        "termRegex": "[WSMF]\\d\\d",
        "termDescription": "quarter"
    },
    threeSchools: [
        { 
            "abbrev": "ucsb",
            "name": "UC Santa Barbara",
            "termRegex": "[WSMF]\\d\\d",
            "termDescription": "quarter"
        },
        {
            "abbrev": "umn",
            "name": "University of Minnesota",
            "termRegex": "[WSMF]\\d\\d",
            "termDescription": "semester"
        },
        {
            "abbrev": "ucsd",
            "name": "UC San Diego",
            "termRegex": "[WSMF]\\d\\d",
            "termDescription": "quarter"
        }
    ]
};


export { schoolsFixtures };