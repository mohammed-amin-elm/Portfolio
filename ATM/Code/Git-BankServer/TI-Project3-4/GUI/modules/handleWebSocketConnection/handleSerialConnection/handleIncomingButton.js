import { GLOBAL, redirectClient } from "../../handleWebSocketConnection.js";
import { handleGeldOpnemenRedirect, handleSelectCashCombinations } from "../handleWebSocketData/handleGeldOpnemen.js";
import { handlePrintReceipt, handleTransaction } from "../handleWebSocketData/handleReceipt.js";
import { handleSelectSnelpinnen, handleSnelpinnen } from "../handleWebSocketData/handleSnelpinnen.js";
import { handleBack, handleUitloggen } from "../handleWebSocketData/handleUitloggen.js";
import { handleGetInfo } from "../handleWebSocketData/handleUserData.js";

export function handleIncomingButton(ws, port, button) {
    switch(GLOBAL.CLIENT_STATE) {
        case "OPTIONS":
            switch(button) {
                case "BTN1":
                    handleGetInfo(ws);
                    break;
                case "BTN2":
                    handleTransaction(ws);
                    break;
                case "BTN4":
                    handleSnelpinnen(ws);
                    break;
                case "BTN5":
                    handleGeldOpnemenRedirect(ws);
                    break;
                case "BTN6":
                    handleUitloggen(ws);
                    break;
            }
            break;
        case "TRANSACTION":
            // TODO: Implement scroll
            switch(button) {
                case "BTN3":
                    handleBack(ws);
                    break;
            }
            break;
        case "GET_INFO":
            if(button == "BTN3") {
                handleBack(ws);
            }
            break;
        case "GELD_OPNEMEN":
            if(button == "BTN3") {
                handleBack(ws);
            }
            break;
        case "PINCODE":
            if(button == "BTN3") {
                handleBack(ws);
            }
            break;
        case "SNELPINNEN":
            switch(button) {
                case "BTN1":
                    handleSelectSnelpinnen(ws, 10);
                    break;
                case "BTN2":
                    handleSelectSnelpinnen(ws, 20);
                    break;
                case "BTN3":
                    handleBack(ws);
                    break;
                case "BTN4":
                    handleSelectSnelpinnen(ws, 50);
                    break;
                case "BTN5":
                    handleSelectSnelpinnen(ws, 70);
                    break;
                case "BTN6":
                    handleSelectSnelpinnen(ws, 100);
                    break;
            }
            break;
        case "CASH_COMBINATION":
            switch(button) {
                case "BTN3":
                    handleBack(ws);
                    break;
                case "BTN4":
                    handleSelectCashCombinations(ws, port, 0);
                    break;
                case "BTN5":
                    handleSelectCashCombinations(ws, port, 1);
                    break;
                case "BTN6":
                    handleSelectCashCombinations(ws, port, 2);
                    break;
            }
            break;
        case "RECEIPT_OPTION":
            switch(button) {
                case "BTN3":
                    handlePrintReceipt(ws, port, false);
                    break;
                case "BTN6":
                    handlePrintReceipt(ws, port, true);
                    break;
            }
            break;
    }
}