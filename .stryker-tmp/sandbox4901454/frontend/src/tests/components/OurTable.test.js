import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import OurTable, { ButtonColumn, DateColumn, HrefButtonColumn, PlaintextColumn} from "main/components/OurTable";
import {getContrastYIQ} from "main/components/OurTable";
import { isValidHexColor } from "main/components/OurTable";

describe("OurTable tests", () => {
    const threeRows = [
        {
            col1: 'Hello',
            col2: 'World',
            createdAt: '2021-04-01T04:00:00.000',
            log: "foo\nbar\n  baz",
        },
        {
            col1: 'react-table',
            col2: 'rocks',
            createdAt: '2022-01-04T14:00:00.000',
            log: "foo\nbar",

        },
        {
            col1: 'whatever',
            col2: 'you want',
            createdAt: '2023-04-01T23:00:00.000',
            log: "bar\n  baz",
        }
    ];
    const clickMeCallback = jest.fn();

    const columns = [
        {
            Header: 'Column 1',
            accessor: 'col1', // accessor is the "key" in the data
        },
        {
            Header: 'Column 2',
            accessor: 'col2',
        },
        ButtonColumn("Click", "primary", clickMeCallback, "testId"),
        DateColumn("Date", (cell) => cell.row.original.createdAt),
        PlaintextColumn("Log", (cell) => cell.row.original.log),
        HrefButtonColumn("TestHrefButtonColumn", "primary", "/test", "testId")
    ];

    test("renders an empty table without crashing", () => {
        render(
            <OurTable columns={columns} data={[]} />
        );
    });

    test("TestHrefButtonColumn works as it should", async () => {
        render(
            <OurTable columns={columns} data={threeRows} />
        );
        expect(await screen.findByTestId("testId-cell-row-0-col-TestHrefButtonColumn-button")).toBeInTheDocument();
        const button = screen.getByTestId("testId-cell-row-0-col-TestHrefButtonColumn-button");
        expect(button).toHaveAttribute("href", "/test");
    });


    test("Button with predefined variant appears in the table and is clickable", async () => {
        render(<OurTable columns={[ButtonColumn("Click", "primary", clickMeCallback, "testId")]} data={threeRows} />);
      
        const button = await screen.findByTestId("testId-cell-row-0-col-Click-button");
        expect(button).toBeInTheDocument();      
        fireEvent.click(button);
        await waitFor(() => expect(clickMeCallback).toBeCalledTimes(1));
      });    

    test("Button with hex color code appears with correct style and is clickable", async () => {
        render(<OurTable columns={[ButtonColumn("Click", "#4CAF50", clickMeCallback, "testId")]} data={threeRows} />);
      
        const button = await screen.findByTestId("testId-cell-row-0-col-Click-button");
        expect(button).toBeInTheDocument();
        expect(button).toHaveStyle(`background-color: #4CAF50`);
        expect(button).toHaveStyle(`color: ${getContrastYIQ("#4CAF50")}`);
        fireEvent.click(button);
        await waitFor(() => expect(clickMeCallback).toBeCalledTimes(1));
      });    

    test("The button appears in the table", async () => {
        render(
            <OurTable columns={columns} data={threeRows} />
        );

        expect(await screen.findByTestId("testId-cell-row-0-col-Click-button")).toBeInTheDocument();
        const button = screen.getByTestId("testId-cell-row-0-col-Click-button");
        fireEvent.click(button);
        await waitFor(() => expect(clickMeCallback).toBeCalledTimes(1));
    });

    test("default testid is testId", async () => {
        render(
            <OurTable columns={columns} data={threeRows} />
        );
        expect(await screen.findByTestId("testid-header-col1")).toBeInTheDocument();
    });

    test("getContrastYIQ returns 'black' for light background colors", () => {
        const lightColorHex = "#FFFFFF"; 
        const textColor = getContrastYIQ(lightColorHex);
        expect(textColor).toBe('black');
    });

    test("getContrastYIQ returns 'white' for dark background colors", () => {
        const darkColorHex = "#000000"; 
        const textColor = getContrastYIQ(darkColorHex);
        expect(textColor).toBe('white');
    });
      
    test("click on a header and a sort caret should appear", async () => {
            render(
                <OurTable columns={columns} data={threeRows} testid={"sampleTestId"} />
            );

            expect(await screen.findByTestId("sampleTestId-header-col1")).toBeInTheDocument();
            const col1Header = screen.getByTestId("sampleTestId-header-col1");

            const col1SortCarets = screen.getByTestId("sampleTestId-header-col1-sort-carets");
            expect(col1SortCarets).toHaveTextContent('');

            const col1Row0 = screen.getByTestId("sampleTestId-cell-row-0-col-col1");
            expect(col1Row0).toHaveTextContent("Hello");

            fireEvent.click(col1Header);
            expect(await screen.findByText("🔼")).toBeInTheDocument();

            fireEvent.click(col1Header);
            expect(await screen.findByText("🔽")).toBeInTheDocument();
        });
    });

    test("getContrastYIQ returns 'black' for color with YIQ exactly at boundary", () => {
        const boundaryColorHex = "#4CAF50"; 
        expect(getContrastYIQ(boundaryColorHex)).toBe('black');
    });

    test("Button with hex color code followed by extra characters does not apply incorrect style", async () => {
        const invalidVariant = "#4CAF50extra"; 
        const clickMeCallback = jest.fn();
        const threeRows = [
            {
                col1: 'Hello',
                col2: 'World',
                createdAt: '2021-04-01T04:00:00.000',
                log: "foo\nbar\n  baz",
            },
            {
                col1: 'react-table',
                col2: 'rocks',
                createdAt: '2022-01-04T14:00:00.000',
                log: "foo\nbar",
    
            },
            {
                col1: 'whatever',
                col2: 'you want',
                createdAt: '2023-04-01T23:00:00.000',
                log: "bar\n  baz",
            }
        ];
        render(<OurTable columns={[ButtonColumn("Click", invalidVariant, clickMeCallback, "testId")]} data={threeRows} />);
      
        const button = await screen.findByTestId("testId-cell-row-0-col-Click-button");
        expect(button).not.toHaveStyle(`background-color: ${invalidVariant}`);
    });

    
    test("getContrastYIQ correctly calculates contrast for color with distinct blue component", () => {
        const colorHex = "#0000FF";
        expect(getContrastYIQ(colorHex)).toBe('white');
    });


    test("getContrastYIQ returns correct contrast for a specific color", () => {
        const colorHex = "#BADA55"; 
        expect(getContrastYIQ(colorHex)).toBe('black'); 
    });

    test("getContrastYIQ correctly calculates contrast for color with distinct red component", () => {
        const colorHex = "#FF0000"; 
        expect(getContrastYIQ(colorHex)).toBe('white');
    });

    test("getContrastYIQ correctly calculates contrast for color with distinct green component", () => {
        const colorHex = "#00FF00";
        expect(getContrastYIQ(colorHex)).toBe('black');
    });

    test("getContrastYIQ returns 'white' for YIQ below 128", () => {
        const boundaryColorHex = "#3C803C"; 
        expect(getContrastYIQ(boundaryColorHex)).toBe('white');
    });
 
    test('getContrastYIQ returns "black" for YIQ exactly at 128', () => {
        const boundaryColor = '#659365';
        expect(getContrastYIQ(boundaryColor)).toBe('black');
    });
      

describe('isValidHexColor', () => {
    test('recognizes valid 3-character hex color codes', () => {
        expect(isValidHexColor('#123')).toBe(true);
    });
    
    test('recognizes valid 6-character hex color codes', () => {
        expect(isValidHexColor('#123456')).toBe(true);
    });
    
    test('rejects hex color codes with extra characters at the start', () => {
        expect(isValidHexColor('extra#123456')).toBe(false);
    });
    
    test('rejects hex color codes with extra characters at the end', () => {
        expect(isValidHexColor('#123456extra')).toBe(false);
    });
    
    test('rejects invalid hex color codes', () => {
        expect(isValidHexColor('#123GHI')).toBe(false);
    });
});

