import * as React from 'react'
import { Text, StyleSheet } from "@react-pdf/renderer";
import {useTheme} from "@mui/material/styles";

const styles = StyleSheet.create({
  container: {
    fontFamily: 'OpenSans',
    fontSize: 10,
  },
})

export const Resume: React.FC<{ style?: any }> = ({ children, style }) => {
  const theme = useTheme()

  return (
    <Text style={[styles.container, { color: theme.palette.grey.A700 }, style!]}>
      {children}
    </Text>
  )
}
