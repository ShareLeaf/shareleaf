import {Configuration} from "../api";

export const headerConfig = (token: string) => {
    const config = new Configuration();
    config.baseOptions = {
        headers: { Authorization: 'Bearer ' + token },
    };
    return config
}