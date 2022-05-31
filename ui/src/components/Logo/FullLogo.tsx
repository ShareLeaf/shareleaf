import {
  Box,
  Tooltip,
  TooltipProps,
  tooltipClasses,
  styled
} from '@mui/material';
import Link from 'src/components/Link';

const LogoWrapper = styled(Link)(
  ({ theme }) => `
        color: ${theme.palette.text.primary};
        display: flex;
        text-decoration: none;
        max-width: 256px;
        margin: 0 auto;
        font-weight: ${theme.typography.fontWeightBold};
`
);

const TooltipWrapper = styled(({ className, ...props }: TooltipProps) => (
  <Tooltip {...props} classes={{ popper: className }} />
))(({ theme }) => ({
  [`& .${tooltipClasses.tooltip}`]: {
    backgroundColor: theme.colors.alpha.trueWhite[100],
    color: theme.palette.getContrastText(theme.colors.alpha.trueWhite[100]),
    fontSize: theme.typography.pxToRem(12),
    fontWeight: 'bold',
    borderRadius: theme.general.borderRadiusSm,
    boxShadow:
      '0 .2rem .8rem rgba(7,9,25,.18), 0 .08rem .15rem rgba(7,9,25,.15)'
  },
  [`& .${tooltipClasses.arrow}`]: {
    color: theme.colors.alpha.trueWhite[100]
  }
}));

function FullLogo() {
  return (
    <TooltipWrapper title={'ShareLeaf'} arrow>
      <LogoWrapper href="/">
          <Box sx={{maxWidth: "164px", margin: "0 auto"}}
              component="img"
              alt="ShareLeaf"
              src="/static/images/logo/shareleaf2.png"
          />
      </LogoWrapper>
    </TooltipWrapper>
  );
}

export default FullLogo;
