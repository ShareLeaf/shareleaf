import * as React from 'react'
import { StyleSheet, View } from "@react-pdf/renderer";
import { Icon, IconProps } from '../icon'
import {useTheme} from "@mui/material/styles";

const styles = StyleSheet.create({
  container: {
    padding: 8,
    flexDirection: 'row',
    borderRadius: 99999,
  },
})

export function RoundedIcon({ style, ...props }: IconProps) {
  const theme = useTheme()

  return (
    <View
      style={[
        styles.container,
        { backgroundColor: theme.palette.grey["200"] },
        style!,
      ]}
    >
      <Icon {...props} />
    </View>
  )
}
