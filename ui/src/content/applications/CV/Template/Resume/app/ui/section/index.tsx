import * as React from 'react'
import { View, Text, StyleSheet } from "@react-pdf/renderer";
import {useTheme} from "@mui/material/styles";

export interface SectionProps {
  style?: any
  title: string
  spacing: number
}

const styles = StyleSheet.create({
  container: {
    paddingBottom: 20,
  },
  title: {
    fontSize: 9,
    textTransform: 'uppercase',
    fontFamily: 'OpenSans',
    fontWeight: 'bold',
  },
  separator: {
    height: 2,
    marginTop: 1,
    marginBottom: 12,
  },
})

export const Section: React.FC<SectionProps> = ({
  style,
  title,
  spacing,
  children,
}) => {
  const theme = useTheme()

  const childCount = React.Children.count(children)

  return (
    <View style={[styles.container, style!]}>
      <Text style={[styles.title, { color: theme.palette.grey["900"] }]}>{title}</Text>
      <View
        style={[styles.separator, { backgroundColor: theme.palette.grey["300"] }]}
      />
      {React.Children.map(children, (child, index) => {
        const mb = index + 1 === childCount ? 0 : spacing
        const style = { marginBottom: mb }

        // @ts-expect-error
        return mb ? React.cloneElement(child, { style }) : child
      })}
    </View>
  )
}
