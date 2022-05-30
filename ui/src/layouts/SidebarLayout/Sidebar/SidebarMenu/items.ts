import { ReactNode } from 'react';

import DesignServicesIcon from '@mui/icons-material/DesignServices';
import BrightnessLowIcon from '@mui/icons-material/BrightnessLow';
import MmsIcon from '@mui/icons-material/Mms';
import TableChartIcon from '@mui/icons-material/TableChart';
import BallotIcon from '@mui/icons-material/Ballot';
import BeachAccessIcon from '@mui/icons-material/BeachAccess';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import FilterVintageIcon from '@mui/icons-material/FilterVintage';
import HowToVoteIcon from '@mui/icons-material/HowToVote';
import LocalPharmacyIcon from '@mui/icons-material/LocalPharmacy';
import RedeemIcon from '@mui/icons-material/Redeem';
import SettingsIcon from '@mui/icons-material/Settings'
import TrafficIcon from '@mui/icons-material/Traffic';
import CopyAllIcon from '@mui/icons-material/CopyAll';
import WebIcon from '@mui/icons-material/Web';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import LightbulbIcon from '@mui/icons-material/Lightbulb';
import SendIcon from '@mui/icons-material/Send';
import SecurityIcon from '@mui/icons-material/Security';
import GavelIcon from '@mui/icons-material/Gavel';
import AddIcon from '@mui/icons-material/Add';
import GroupsIcon from '@mui/icons-material/Groups';

export interface MenuItem {
  link?: string;
  icon?: ReactNode;
  badge?: string;
  items?: MenuItem[];
  name: string;
}

export interface MenuItems {
  items: MenuItem[];
  heading: string;
}

const menuItems: MenuItems[] = [
  {
    heading: 'Curriculum Vitae',
    items: [
      {
        name: 'Resumes',
        link: '/cv/view/resumes',
        icon: WebIcon
      },
      {
        name: 'Cover Letters',
        link: '/cv/view/cover-letters',
        icon: CopyAllIcon
      },
      {
        name: 'Create Resume',
        link: '/editor/doc?key=zzzzzaaaaffff',
        icon: AddIcon
      },
    ]
  },
  {
    heading: 'Help',
    items: [
      {
        name: 'About',
        link: '/help/about',
        icon: GroupsIcon
      },
      {
        name: 'FAQs',
        link: '/help/faqs',
        icon: LightbulbIcon
      },
      {
        name: 'Contact Us',
        link: '/help/contact-us',
        icon: SendIcon
      },
    ]
  },
  {
    heading: 'Legal',
    items: [
      {
        name: 'Privacy Policy',
        link: '/legal/privacy',
        icon: SecurityIcon
      },
      {
        name: 'Terms of Service',
        link: '/legal/tos',
        icon: GavelIcon
      },
    ]
  }

];

export default menuItems;
