import { useCallback, useEffect, useRef, useState } from "react";
import { Link, useLocation } from "react-router";

// Assume these icons are imported from an icon library
import {
  BoxCubeIcon,
  CalenderIcon,
  ChevronDownIcon,
  GridIcon,
  HorizontaLDots,
  PieChartIcon,
  UserCircleIcon,
} from "../icons";
import { useSidebar } from "../hooks/useSidebar";
// import SidebarWidget from "./SidebarWidget";
import { RootState } from "../redux/store";
import { useDispatch, useSelector } from "react-redux";
import { ChevronDown, Check } from "lucide-react";
import { setCurrentWorkspace, WorkspaceMember } from "../redux/workspaceSlice";

type NavItem = {
  name: string;
  icon: React.ReactNode;
  path?: string;
  subItems?: { name: string; path: string; pro?: boolean; new?: boolean }[];
};

const generalOptions: NavItem[] = [
  {
    icon: <GridIcon />,
    name: "Links",
    path: "/",
  },
  {
    icon: <CalenderIcon />,
    name: "Analytics",
    path: "/analytics",
  },
];

const ownerOptions: NavItem[] = [
  {
    icon: <BoxCubeIcon />,
    name: "Settings",
    path: "/bar-chart",
  },
];

const WorkspacesDropdown = () => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const dispatch = useDispatch();
  //   name: "kallol",
  //   free: true,
  // });

  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );

  const workspaces = useSelector(
    (state: RootState) => state.workspace.workspaces
  );

  // console.log(currentWorkspace);

  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }

    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [isOpen]);

  // Function to handle worksapce change by the user
  const handleWorksapceChange = (workspace: WorkspaceMember) => {
    dispatch(setCurrentWorkspace(workspace));
    setIsOpen(false);
  };

  return (
    <div className="relative mb-2" ref={dropdownRef}>
      {/* Selected Workspace Button */}
      <button
        className="flex items-center justify-between gap-2 px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200 w-full overflow-hidden dark:bg-white/2 dark:text-gray-300 dark:hover:bg-white/5 dark:hover:text-gray-300"
        onClick={() => setIsOpen((prev) => !prev)}
      >
        <div className="flex items-center gap-2 w-full overflow-hidden">
          {/* Workspace Icon */}
          <div className="w-8 h-8 flex items-center justify-center bg-pink-500 text-white rounded-full shrink-0">
            {currentWorkspace?.workspaceId.name.charAt(0).toUpperCase()}
          </div>

          {/* Workspace Name and Type */}
          <div className="flex flex-col overflow-hidden">
            <p className="text-sm font-medium truncate w-full text-start">
              {currentWorkspace?.workspaceId.name}
            </p>
            <p className="text-xs text-gray-500 truncate w-full text-start">
              {currentWorkspace?.workspaceId.type}
            </p>
          </div>
        </div>

        {/* Dropdown Icon */}
        <ChevronDown className="w-4 h-4 text-gray-500 shrink-0" />
      </button>

      {/* Dropdown Menu */}
      {isOpen && (
        <div className="absolute mt-2 w-full bg-white shadow-lg rounded-lg py-2 z-50 dark:shadow-lg dark:bg-black dark:text-gray-300 ">
          <div className="px-4 py-2 text-sm text-gray-900 dark:text-white">
            Workspaces
          </div>
          {workspaces.map((workspace) => (
            <button
              key={workspace.workspaceId.name}
              className={`flex items-center justify-between w-full px-4 py-2 text-sm ${
                currentWorkspace?.workspaceId.name ===
                workspace.workspaceId.name
                  ? "bg-gray-200 dark:bg-gray-700"
                  : "hover:bg-gray-100 dark:hover:bg-gray-500"
              }`}
              onClick={() => {
                handleWorksapceChange(workspace);
              }}
            >
              <div className="flex items-center gap-2 w-full overflow-hidden">
                <div className="w-8 h-8 flex items-center justify-center bg-pink-500 text-white rounded-full shrink-0">
                  {workspace.workspaceId.name.charAt(0).toUpperCase()}
                </div>
                <div className="flex flex-col overflow-hidden">
                  <p className="text-sm font-medium truncate w-full text-start">
                    {workspace?.workspaceId.name}
                  </p>
                  <p className="text-xs text-gray-500 truncate w-full text-start">
                    {workspace?.workspaceId.type}
                  </p>
                </div>
              </div>
              {currentWorkspace?.workspaceId.name ===
                workspace.workspaceId.name && (
                <Check className="w-4 h-4 text-gray-700" />
              )}
            </button>
          ))}

          {/* Create New Workspace Option */}
          <button className="w-full px-4 py-2 text-sm text-blue-500 hover:bg-gray-100 dark:hover:bg-gray-700">
            + Create new workspace
          </button>
        </div>
      )}
    </div>
  );
};

const AppSidebar: React.FC = () => {
  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );

  const { isExpanded, isMobileOpen } = useSidebar();
  const location = useLocation();

  // const [openSubmenu, setOpenSubmenu] = useState<{
  //   type: "general" | "owner";
  //   index: number;
  // } | null>(null);
  // const [subMenuHeight, setSubMenuHeight] = useState<Record<string, number>>(
  //   {}
  // );
  // const subMenuRefs = useRef<Record<string, HTMLDivElement | null>>({});

  // const isActive = (path: string) => location.pathname === path;
  const isActive = useCallback(
    (path: string) => location.pathname === path,
    [location.pathname]
  );

  useEffect(() => {
    // let submenuMatched = false;
    ["main", "others"].forEach((menuType) => {
      const items = menuType === "main" ? generalOptions : ownerOptions;
      items.forEach((nav, index) => {
        if (nav.subItems) {
          nav.subItems.forEach((subItem) => {
            if (isActive(subItem.path)) {
              // setOpenSubmenu({
              //   type: menuType as "general" | "owner",
              //   index,
              // });
              // submenuMatched = true;
            }
          });
        }
      });
    });

    // if (!submenuMatched) {
    //   setOpenSubmenu(null);
    // }
  }, [location, isActive]);

  // useEffect(() => {
  //   if (openSubmenu !== null) {
  //     const key = `${openSubmenu.type}-${openSubmenu.index}`;
  //     if (subMenuRefs.current[key]) {
  //       setSubMenuHeight((prevHeights) => ({
  //         ...prevHeights,
  //         [key]: subMenuRefs.current[key]?.scrollHeight || 0,
  //       }));
  //     }
  //   }
  // }, [openSubmenu]);

  // const handleSubmenuToggle = (index: number, menuType: "general" | "owner") => {
  //   setOpenSubmenu((prevOpenSubmenu) => {
  //     if (
  //       prevOpenSubmenu &&
  //       prevOpenSubmenu.type === menuType &&
  //       prevOpenSubmenu.index === index
  //     ) {
  //       return null;
  //     }
  //     return { type: menuType, index };
  //   });
  // };

  // render items
  // const renderMenuItems = (items: NavItem[], menuType: "general" | "owner") => (
  const renderMenuItems = (items: NavItem[]) => (
    <ul className="flex flex-col gap-4">
      {items.map((nav) => (
        <li key={nav.name}>
          {nav.path && (
            <Link
              to={nav.path}
              className={`menu-item group ${
                isActive(nav.path) ? "menu-item-active" : "menu-item-inactive"
              }`}
            >
              <span
                className={`menu-item-icon-size ${
                  isActive(nav.path)
                    ? "menu-item-icon-active"
                    : "menu-item-icon-inactive"
                }`}
              >
                {nav.icon}
              </span>
              {(isExpanded || isMobileOpen) && (
                <span className="menu-item-text">{nav.name}</span>
              )}
            </Link>
          )}

          {/* {nav.subItems ? (
            <button
              // onClick={() => handleSubmenuToggle(index, menuType)}
              className={`menu-item group ${
                openSubmenu?.type === menuType && openSubmenu?.index === index
                  ? "menu-item-active"
                  : "menu-item-inactive"
              } cursor-pointer ${
                !isExpanded ? "lg:justify-center" : "lg:justify-start"
              }`}
            >
              <span
                className={`menu-item-icon-size  ${
                  openSubmenu?.type === menuType && openSubmenu?.index === index
                    ? "menu-item-icon-active"
                    : "menu-item-icon-inactive"
                }`}
              >
                {nav.icon}
              </span>
              {(isExpanded || isMobileOpen) && (
                <span className="menu-item-text">{nav.name}</span>
              )}
              {(isExpanded || isMobileOpen) && (
                <ChevronDownIcon
                  className={`ml-auto w-5 h-5 transition-transform duration-200 ${
                    openSubmenu?.type === menuType &&
                    openSubmenu?.index === index
                      ? "rotate-180 text-brand-500"
                      : ""
                  }`}
                />
              )}
            </button>
          ) : (
            nav.path && (
              <Link
                to={nav.path}
                className={`menu-item group ${
                  isActive(nav.path) ? "menu-item-active" : "menu-item-inactive"
                }`}
              >
                <span
                  className={`menu-item-icon-size ${
                    isActive(nav.path)
                      ? "menu-item-icon-active"
                      : "menu-item-icon-inactive"
                  }`}
                >
                  {nav.icon}
                </span>
                {(isExpanded || isMobileOpen) && (
                  <span className="menu-item-text">{nav.name}</span>
                )}
              </Link>
            )
          )}
          {nav.subItems && (isExpanded || isMobileOpen) && (
            <div
              ref={(el) => {
                subMenuRefs.current[`${menuType}-${index}`] = el;
              }}
              className="overflow-hidden transition-all duration-300"
              style={{
                height:
                  openSubmenu?.type === menuType && openSubmenu?.index === index
                    ? `${subMenuHeight[`${menuType}-${index}`]}px`
                    : "0px",
              }}
            >
              <ul className="mt-2 space-y-1 ml-9">
                {nav.subItems.map((subItem) => (
                  <li key={subItem.name}>
                    <Link
                      to={subItem.path}
                      className={`menu-dropdown-item ${
                        isActive(subItem.path)
                          ? "menu-dropdown-item-active"
                          : "menu-dropdown-item-inactive"
                      }`}
                    >
                      {subItem.name}
                      <span className="flex items-center gap-1 ml-auto">
                        {subItem.new && (
                          <span
                            className={`ml-auto ${
                              isActive(subItem.path)
                                ? "menu-dropdown-badge-active"
                                : "menu-dropdown-badge-inactive"
                            } menu-dropdown-badge`}
                          >
                            new
                          </span>
                        )}
                        {subItem.pro && (
                          <span
                            className={`ml-auto ${
                              isActive(subItem.path)
                                ? "menu-dropdown-badge-active"
                                : "menu-dropdown-badge-inactive"
                            } menu-dropdown-badge`}
                          >
                            pro
                          </span>
                        )}
                      </span>
                    </Link>
                  </li>
                ))}
              </ul>
            </div>
          )} */}
        </li>
      ))}
    </ul>
  );

  const getDate = (data: string): string => {
    const date = new Date(data);

    const options: Intl.DateTimeFormatOptions = {
      month: "short",
      day: "2-digit",
      year: "numeric",
    };
    const formattedDate = date.toLocaleDateString("en-US", options);

    return formattedDate;
  };

  return (
    <aside
      className={`fixed mt-16 flex flex-col lg:mt-0 top-0 px-5 left-0 bg-white dark:bg-gray-900 dark:border-gray-800 text-gray-900 h-screen transition-all duration-300 ease-in-out z-50 border-r border-gray-200 
        ${isExpanded || isMobileOpen ? "w-[290px]" : "w-[90px]"}
        ${isMobileOpen ? "translate-x-0" : "-translate-x-full"}
        lg:translate-x-0`}
    >
      {/* LOGO */}
      <div
        className={`py-8 flex ${
          !isExpanded ? "lg:justify-center" : "justify-start"
        }`}
      >
        <Link to="/">
          {isExpanded || isMobileOpen ? (
            <>
              <img
                className="dark:hidden"
                src="/images/logo/logo.svg"
                alt="Logo"
                width={200}
                // height={40}
              />
              <img
                className="hidden dark:block"
                src="/images/logo/logo-dark.svg"
                alt="Logo"
                width={200}
                // height={40}
              />
            </>
          ) : (
            <img
              src="/images/logo/logo-icon.svg"
              alt="Logo"
              width={50}
              // height={50}
            />
          )}
        </Link>
      </div>

      {/* if current workspace not set the show loading */}
      {currentWorkspace === null ? (
        <div>loading</div>
      ) : (
        <div className="flex flex-col gap-10 h-max">
          <div>
            {(isExpanded || isMobileOpen) && <WorkspacesDropdown />}

            <div className="flex flex-col overflow-y-auto duration-300 ease-linear no-scrollbar">
              <nav className="mb-6">
                <div className="flex flex-col gap-4">
                  {/* General options for both owner and member */}
                  <div>
                    <h2
                      className={`mb-4 text-xs uppercase flex leading-[20px] text-gray-400 ${
                        !isExpanded ? "lg:justify-center" : "justify-start"
                      }`}
                    >
                      {isExpanded || isMobileOpen ? (
                        "general"
                      ) : (
                        <HorizontaLDots className="size-6" />
                      )}
                    </h2>
                    {renderMenuItems(generalOptions)}
                  </div>

                  {/* Owner options */}
                  {currentWorkspace.memberType === "OWNER" && (
                    <div className="">
                      <h2
                        className={`mb-4 text-xs uppercase flex leading-[20px] text-gray-400 ${
                          !isExpanded ? "lg:justify-center" : "justify-start"
                        }`}
                      >
                        {isExpanded || isMobileOpen ? (
                          "Others"
                        ) : (
                          <HorizontaLDots className="size-6" />
                        )}
                      </h2>
                      {renderMenuItems(ownerOptions)}
                    </div>
                  )}
                </div>
              </nav>
              {/* {isExpanded || isMobileOpen
            ? currentWorkspace.workspaceId.type !== "FREE" && <SidebarWidget />
            : null} */}
            </div>
          </div>

          {(isExpanded || isMobileOpen) && (
            <div className=" w-full">
              {/* Sidebar Content */}
              <div className="flex-1 overflow-y-auto">
                {/* Usage Section */}

                <div className="flex justify-between text-sm font-medium">
                  <span className="flex items-center gap-2 text-black dark:text-white">
                    <span>Links</span>
                  </span>
                  <span className="text-gray-500">
                    {currentWorkspace.workspaceId.createdLinksThisMonth} of{" "}
                    {currentWorkspace.workspaceId.linkCreationLimitPerMonth}
                  </span>
                </div>
                <p className="text-xs text-gray-400">
                  Usage will reset{" "}
                  {getDate(currentWorkspace.workspaceId.nextResetDate)}
                </p>
              </div>

              {/* Fixed Button at Bottom */}
              {currentWorkspace.workspaceId.type === "FREE" && (
                <button className="w-full mt-4 py-2 bg-black text-white font-semibold rounded-lg hover:bg-gray-800 dark:bg-white dark:text-black">
                  Get QuickShort Pro
                </button>
              )}
            </div>
          )}
        </div>
      )}
    </aside>
  );
};

export default AppSidebar;
