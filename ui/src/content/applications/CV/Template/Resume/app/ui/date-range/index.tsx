import dateFnsFormat from 'date-fns/format'
import dateFnsParse from 'date-fns/parse'
import { IconText } from '../icon-text'

export interface DateRangeProps {
  style?: any
  startAt: string
  endAt?: string
}

export function formatDate(dateString: string, format: string = 'LL/yyyy') {
  const formatString = 'yyyy-MM-dd'
  const referenceDate = new Date()

  return dateFnsFormat(
    dateFnsParse(dateString, formatString, referenceDate),
    format
  )
}

export function DateRange({ startAt, endAt, style }: DateRangeProps) {

  const dateRangeText = [
    formatDate(startAt),
    endAt ? formatDate(endAt) : "In Progress",
  ].join(' - ')

  return <IconText style={style} text={dateRangeText} iconName="calendar" />
}
