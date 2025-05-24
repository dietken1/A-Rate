import Logo from "./Logo";
import { Link, useLocation } from "react-router-dom";

const sidebarMenus = [
  { to: "/", icon: "/icons/chart-line-bold.svg", label: "홈" },
  { to: "/rating", icon: "/icons/book-bookmark-bold.svg", label: "강의평가" },
  { to: "/timetable", icon: "/icons/calendar-blank-bold.svg", label: "시간표" },
  { to: "/resource", icon: "/icons/chats-bold.svg", label: "자료집" },
  { to: "/mentoring", icon: "/icons/trophy-bold.svg", label: "멘토링" },
  { to: "/activity", icon: "/icons/trophy-bold.svg", label: "대외활동" },
];

const Sidebar = ({ className }: { className?: string }) => {
  const location = useLocation();

  return (
    <div
      className={`w-[258px] h-full flex flex-col items-center gap-1 p-1 ${className}`}
    >
      <Logo className="px-[20px] my-[30px] w-full" />
      {sidebarMenus.map((menu) => (
        <SidebarButton
          key={menu.to}
          isClicked={location.pathname === menu.to}
          as={Link}
          to={menu.to}
        >
          <img src={menu.icon} alt={menu.label} className="w-[24px] h-[24px]" />
          <span className="text-sm">{menu.label}</span>
        </SidebarButton>
      ))}
    </div>
  );
};

const SidebarButton = ({
  children,
  onClick,
  isClicked,
  as: Component = "button",
  to,
}: {
  children?: React.ReactNode;
  onClick?: () => void;
  isClicked?: boolean;
  as?: React.ElementType;
  to?: string;
}) => {
  return (
    <Component
      onClick={onClick}
      to={to}
      className={`flex items-center gap-[14px] w-[238px] text-start p-[12px] rounded-md ${
        isClicked ? "bg-white font-bold" : ""
      } hover:bg-white duration-fast`}
    >
      {children}
    </Component>
  );
};

export default Sidebar;
