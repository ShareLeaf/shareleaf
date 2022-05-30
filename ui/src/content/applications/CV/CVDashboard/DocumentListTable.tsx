import React, {ChangeEvent, FC, useEffect, useState} from 'react';
import { format } from 'date-fns';
import {
  Divider,
  Box,
  Card,
  Checkbox,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TablePagination,
  TableRow,
  TableContainer,
  Typography, Grid, Link,
} from '@mui/material';
import LockOpenTwoToneIcon from '@mui/icons-material/LockOpenTwoTone';
import LockTwoToneIcon from '@mui/icons-material/LockTwoTone';

import Label from 'src/components/Label';
import Actions from './Actions';
import {observer} from "mobx-react";
import {styled} from "@mui/material/styles";
import {RRCVMetadata} from "../../../../api";
import {globals} from "../../../../utils/globalSettings";
import {CoverLetterStore, ResumeListStore} from "../../../../store/Store";

type ColorType = 'primary' | 'secondary' | 'error' | 'warning' | 'success' | 'info';

const getTags = (tags: string[]): JSX.Element => {
  const randomColor = () : ColorType => {
    const colors = ["primary", "secondary", "success", "error", "warning", "info"];
    return colors[Math.floor(Math.random() * colors.length)] as ColorType;
  }
  const elements = tags.map((tag, index) =>
      <Grid item xs={12} key={"tag-" + index} sx={{
        // marginBottom: '0.5rem',
        maxHeight: 'none'
      }}>
      <Label color={randomColor()}>{tag}</Label>
      </Grid>);
  if (elements && elements.length > 0) {
    return <Grid  container
                  direction="row"
                  justifyContent="left"
                  alignItems="stretch"
                  spacing={2}
                  sx={{maxWidth: 'fit-content'}}>{elements}</Grid>
  }
  return null;
}

const getStatusIcon = (readOnly: boolean): JSX.Element => {
  if (readOnly) {
    return (
        <RedIconButton>
          <LockTwoToneIcon fontSize="small" />
        </RedIconButton>
    )
  }
  return (
      <GreenIconButton>
        <LockOpenTwoToneIcon fontSize="small" />
      </GreenIconButton>
  )
}


const applyPagination = (
  records: Array<RRCVMetadata>,
  page: number,
  limit: number
): Array<RRCVMetadata> => {
  return records.slice(page * limit, page * limit + limit);
};

const RedIconButton = styled(IconButton)(
    ({ theme }) => `
        &:hover: { background: ${theme.colors.error.lighter} };
        color: ${theme.palette.error.main};
        color: "inherit";
        size: "small";
`
);

const GreenIconButton = styled(IconButton)(
    ({ theme }) => `
        &:hover: { background: ${theme.colors.success.lighter} };
        color: ${theme.palette.success.main};
        color: "inherit";
        size: "small";
`
);

interface DocumentListTableProps {
  records: Array<RRCVMetadata>,
  store: ResumeListStore | CoverLetterStore,
  onGetPaginatedRecords: (paginated?: boolean) => void
}

const DocumentListTable: FC<DocumentListTableProps> = observer((props) => {
  const [pageNum, setPageNum] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(10);
  const [totalRecords, setTotalRecords] = useState<number>(0);
  const [records, setRecords] = useState<Array<RRCVMetadata>>([]);
  const [selectedRecordKey, setRecordKey] = useState<string>("");
  const [selectedRecordObject, setSelectedRecordObject] = useState<RRCVMetadata | undefined>(undefined);

  const handleSelectOneRecord = (event: ChangeEvent<HTMLInputElement>, record: RRCVMetadata): void => {
    if (selectedRecordKey === "") {
      setRecordKey(record.cv_key);
      setSelectedRecordObject(record);
    } else {
      setRecordKey("")
      setSelectedRecordObject(undefined);
    }
  };

  const handlePageChange = (event: any, newPage: number): void => {
    props.store.setPageNum(newPage);
    setPageNum(newPage);
    if (records.length < newPage * pageSize + pageSize) {
      props.onGetPaginatedRecords(true);
    }

  };

  const handleLimitChange = (event: ChangeEvent<HTMLInputElement>): void => {
    const newPageSize = parseInt(event.target.value);
    props.store.setPageSize(newPageSize);
    props.store.setPageNum(0);
    setPageSize(newPageSize);
    setPageNum(0);
    props.onGetPaginatedRecords(false);
  };

    const paginatedRecords = applyPagination(
        records,
        pageNum,
        pageSize
    );
    useEffect(() => {
      setRecords(props.records);
      setPageSize(props.store.pageSize);
      setPageNum(props.store.pageNum);
      setTotalRecords(props.store.totalRecords);
    }, [records])

    return (
        <>
          {paginatedRecords.length > 0 &&
              <Card>
                {selectedRecordKey && (
                    <Box flex={1} p={2}>
                      <Actions
                          record={selectedRecordObject}
                          onGetPaginatedRecords={props.onGetPaginatedRecords}
                      />
                    </Box>
                )}
                <Divider/>
                <TableContainer>
                  <Table>
                    <TableHead>
                      <TableRow>
                        <TableCell padding="checkbox">
                        </TableCell>
                        <TableCell sx={{textAlign: 'left'}}>Name</TableCell>
                        <TableCell sx={{textAlign: 'left'}}>Tags</TableCell>
                        <TableCell sx={{textAlign: 'left'}}>Status</TableCell>
                        <TableCell sx={{textAlign: 'left'}}>Updated</TableCell>
                        <TableCell sx={{textAlign: 'left'}}>Created</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {paginatedRecords.map((record) => {
                        const isRecordSelected = selectedRecordKey === record.cv_key;
                        return (
                            <TableRow
                                hover
                                key={record.cv_key}
                                selected={isRecordSelected}
                            >
                              <TableCell padding="checkbox" sx={{textAlign: 'left'}}>
                                <Checkbox
                                    sx={{textAlign: 'left', minWidth: '96px'}}
                                    color="primary"
                                    checked={isRecordSelected}
                                    disabled={selectedRecordKey !== "" && isRecordSelected === false}
                                    onChange={(event: ChangeEvent<HTMLInputElement>) =>
                                        handleSelectOneRecord(event, record)
                                    }
                                    value={isRecordSelected}
                                />
                              </TableCell>
                              <TableCell sx={{textAlign: 'left', paddingRight: '1rem'}}>
                                <Typography
                                    component="span"
                                    variant="h4"
                                    fontWeight="bold"
                                    color="text.primary"
                                    gutterBottom
                                    noWrap
                                >
                                  <Link href={"/document/" + record.cv_key} underline="none">{record.cv_title}</Link>
                                </Typography>
                              </TableCell>

                              <TableCell sx={{textAlign: 'left'}}>
                                <Typography
                                    variant="body1"
                                    fontWeight="bold"
                                    color="text.primary"
                                    gutterBottom
                                >
                                  {getTags(record.tags)}
                                </Typography>
                              </TableCell>


                              <TableCell sx={{textAlign: 'left'}}>
                                {getStatusIcon(record.read_only)}
                              </TableCell>

                              <TableCell sx={{textAlign: 'left'}}>
                                <Typography variant="body2" color="text.secondary" noWrap>
                                  {format(Date.parse(record.updated_at), globals.dateFormat)}
                                </Typography>
                              </TableCell>

                              <TableCell sx={{textAlign: 'left'}}>
                                <Typography variant="body2" color="text.secondary" noWrap>
                                  {format(Date.parse(record.created_at), globals.dateFormat)}
                                </Typography>
                              </TableCell>
                            </TableRow>
                        );
                      })}
                    </TableBody>
                  </Table>
                </TableContainer>
                <Box p={2}>
                  <TablePagination
                      component="div"
                      count={totalRecords}
                      onPageChange={handlePageChange}
                      onRowsPerPageChange={handleLimitChange}
                      page={pageNum}
                      rowsPerPage={pageSize}
                      rowsPerPageOptions={[5, 10, 25, 30]}
                  />
                </Box>
              </Card>
          }
        </>
    );
});

export default DocumentListTable;
