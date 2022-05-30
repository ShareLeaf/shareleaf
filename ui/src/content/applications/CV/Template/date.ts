import moment from "moment";

const monthMap = {
    0: "Jan",
    1: "Feb",
    2: "Mar",
    3: "Apr",
    4: "May",
    5: "Jun",
    6: "Jul",
    7: "Aug",
    8: "Sep",
    9: "Oct",
    10: "Nov",
    11: "Dec"
}

export const getFormattedDate = (date: string): string => {
    if (date) {
        const parsedDate: moment.Moment = moment(date)
        return monthMap[parsedDate.month()] + " " + parsedDate.year();
    }
    return "Present";
}

export const parseDate = (date: string): string => {
    if (date) {
        return moment(date).format("YYYY-MM-DD");
    }
    return null;
}