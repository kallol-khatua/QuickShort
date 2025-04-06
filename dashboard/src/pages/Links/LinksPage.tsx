import React, { useEffect, useState } from "react";
import { RootState } from "../../redux/store";
import { useDispatch, useSelector } from "react-redux";
import RotatingLoader from "../../components/ui/loader/RotatingLoader";
import axiosWorkspaceInstance from "../../axiosWorkspaceInstance";
import axios from "axios";
import { ErrorApiResponse } from "../../helper/ErrorApiResponse";
import toast from "react-hot-toast";
import { logout } from "../../redux/authSlice";
import { useNavigate } from "react-router-dom";
import { SuccessApiResponse } from "../../helper/SuccessApiResponse";
import { FaCopy } from "react-icons/fa";
import { FiMoreVertical } from "react-icons/fi";
import { ChevronDown } from "lucide-react";
import PageBreadcrumb from "../../components/common/PageBreadCrumb";
import CreateLink from "./CreateLink";

import Badge from "../../components/ui/badge/Badge";

interface LinksType {
  id: string;
  originalUrl: string;
  shortCode: string;
  expiresAt: string | null;
  status: string; // "ACTIVE"
  createdAt: string;
  updatedAt: string;
  active: boolean;
}

interface LinksResponseData extends SuccessApiResponse {
  data: {
    content: LinksType[];
    first: boolean;
    last: boolean;
    empty: boolean;
    number: number;
    numberOfElements: number;
    size: number;
    totalElements: number;
    totalPages: number;
  };
}

const ListLinks: React.FC<{
  links: LinksType[];
  number: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  totalElements: number;
  totalPages: number;
  size: number;
  handleNumberChange: (num: number) => void;
}> = ({
  links,
  number,
  numberOfElements,
  first,
  last,
  totalElements,
  totalPages,
  size,
  handleNumberChange,
}) => {
  const handleLinkCopy = async (link: string) => {
    toast.success("Copied short link to clipboard!");
    await navigator.clipboard.writeText(
      `${import.meta.env.VITE_URL_TRACKING_BASE_URL}/${link}`
    );
  };
  return (
    <>
      <div className="dark:bg-gray-900 rounded-lg">
        {links.map((link) => (
          <div
            key={link.id}
            className="border bg-white dark:border-gray-700 dark:bg-gray-800 rounded-lg p-4 mb-3 flex justify-between items-center"
          >
            <div className="flex items-center">
              <div>
                <div className="flex items-center space-x-2">
                  <span className="font-semibold dark:text-white">
                    {import.meta.env.VITE_URL_TRACKING_BASE_URL}/
                    {link.shortCode}
                  </span>
                  <FaCopy
                    className="text-gray-500 dark:text-gray-400 cursor-pointer"
                    onClick={() => handleLinkCopy(link.shortCode)}
                  />
                  {link.active ? (
                    <Badge variant="solid" color="success">
                      {link.status}
                    </Badge>
                  ) : (
                    <Badge variant="solid" color="error">
                      {link.status}
                    </Badge>
                  )}
                </div>
                <p className="text-gray-500 dark:text-gray-400 text-sm truncate max-w-[250px] sm:max-w-[350px] md:max-w-[550px] overflow-hidden">
                  {link.originalUrl}
                </p>
              </div>
            </div>

            {/* triple dot icon */}
            <div className="text-gray-500 dark:text-gray-400 flex items-center">
              <FiMoreVertical className="cursor-pointer ml-3" />
            </div>
          </div>
        ))}

        <div className="flex justify-between items-center mt-5 flex-col md:flex-row">
          <div className="text-gray-700 dark:text-gray-300">
            Showing {number * size + 1} to {number * size + numberOfElements} of{" "}
            {totalElements} entries
          </div>

          {/* Pagination */}
          <div className="flex items-center justify-end space-x-2 text-gray-700 dark:text-gray-300">
            {/* Previous Button */}
            <button
              onClick={() => handleNumberChange(number - 1)}
              disabled={first}
              className={`px-3 py-2 border dark:border-gray-600 rounded-lg transition ${
                first
                  ? "text-gray-400 cursor-not-allowed"
                  : "hover:bg-gray-100 dark:hover:bg-gray-700 dark:text-white"
              }`}
            >
              Previous
            </button>

            {/* Page Numbers */}
            {[...Array(totalPages)].map((_, index) => (
              <button
                key={index}
                onClick={() => handleNumberChange(index)}
                className={`px-3 py-2 border dark:border-gray-600 rounded-lg transition ${
                  number === index
                    ? "bg-blue-600 text-white dark:bg-blue-500"
                    : "hover:bg-gray-100 dark:hover:bg-gray-700 dark:text-white"
                }`}
              >
                {index + 1}
              </button>
            ))}

            {/* Next Button */}
            <button
              onClick={() => handleNumberChange(number + 1)}
              disabled={last}
              className={`px-3 py-2 border dark:border-gray-600 rounded-lg transition ${
                last
                  ? "text-gray-400 cursor-not-allowed"
                  : "hover:bg-gray-100 dark:hover:bg-gray-700 dark:text-white"
              }`}
            >
              Next
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

const LinksPage: React.FC = () => {
  const [isLinksLoaded, setIsLinksLoaded] = useState(false);
  const dispatch = useDispatch();
  const naviagate = useNavigate();
  const [reload, setReload] = useState(false);
  const options = [5, 10, 20];

  const [links, setLinks] = useState<LinksType[]>([]);
  const [number, setNumber] = useState(0);
  const [size, setSize] = useState(10);
  const [numberOfElements, setNumberOfElements] = useState(0);
  const [first, setFirst] = useState(false);
  const [last, setLast] = useState(false);
  const [empty, setEmpty] = useState(false);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  //  create link modal
  const [isCreateLinkModalOpen, setIsCreateLinkModalOpen] = useState(false);

  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );

  // Load all linked from Backend
  useEffect(() => {
    const loadLinks = async () => {
      try {
        // console.log("Loading links");
        // console.log(currentWorkspace.workspaceId.id);
        const response = await axiosWorkspaceInstance.get<LinksResponseData>(
          `${currentWorkspace?.workspaceId?.id}/shorten-url?page=${number}&size=${size}`
        );

        // console.log(response.data.data);
        setLinks(response.data.data.content);
        setNumber(response.data.data.empty ? 0 : response.data.data.number);
        setNumberOfElements(response.data.data.numberOfElements);
        setFirst(response.data.data.first);
        setLast(response.data.data.last);
        setEmpty(response.data.data.empty);
        setTotalElements(response.data.data.totalElements);
        setTotalPages(response.data.data.totalPages);

        setTimeout(() => {
          setIsLinksLoaded(true);
          setReload(false);
        }, 250);
      } catch (err: unknown) {
        //   console.log(err);

        if (axios.isAxiosError(err) && err.response) {
          const errorData: ErrorApiResponse = err.response.data;
          toast.error(errorData.message);

          // if unauthorized the logout using auth slice, protected route will take to signin page
          if (errorData.status_code === 401) {
            dispatch(logout());
          } else {
            naviagate("/error");
          }
        } else {
          console.error("Unexpected error:", err);
          naviagate("/error");
        }
      }
    };
    loadLinks();
  }, [
    currentWorkspace,
    dispatch,
    isLinksLoaded,
    naviagate,
    number,
    reload,
    size,
  ]);

  const handleSizeChange = (num: number) => {
    setSize(num);
    setReload(true);
  };

  const handleNumberChange = (num: number) => {
    setNumber(num);
    setReload(true);
  };

  const handleLinkCreateModalToggle = () => {
    setIsCreateLinkModalOpen((prev) => !prev);
  };

  const handleLinkReload = () => {
    setReload(true);
  };

  return (
    <div className="min-h-full flex flex-col">
      {/* Page Header */}
      <PageBreadcrumb pageTitle="Links" />

      {/* Main Content: Takes full height after breadcrumb */}
      <div className="flex flex-col flex-1 min-h-0">
        {/* Search and Filter Section */}
        <div className="w-full">
          <div className="flex justify-between mb-4">
            {/* <div className="flex gap-2 ">
              <button className="border px-4 py-2 rounded-lg flex items-center dark:bg-white dark:text-black">
                Filter
              </button>
              <button className="border px-4 py-2 rounded-lg flex items-center dark:bg-white dark:text-black">
                Display
              </button>
            </div> */}

            <div className="flex items-center space-x-2 text-gray-700 dark:text-gray-300">
              <span>Show</span>
              <div className="relative flex justify-between">
                <select
                  title="size"
                  value={size}
                  onChange={(e) => handleSizeChange(Number(e.target.value))}
                  className="border rounded-lg px-4 py-1.5 appearance-none text-gray-700 dark:text-gray-300 dark:bg-gray-800 dark:border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:focus:ring-blue-400"
                >
                  {options.map((option) => (
                    <option key={option} value={option}>
                      {option}
                    </option>
                  ))}
                </select>
                <ChevronDown
                  className="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-500 dark:text-gray-400 pointer-events-none"
                  size={16}
                />
              </div>
              <span>entries</span>
            </div>

            <button
              className="flex items-center justify-center p-3 font-medium text-white rounded-lg bg-gray-900 text-theme-sm hover:bg-gray-800 dark:bg-white dark:text-black dark:bg-gray-300"
              onClick={handleLinkCreateModalToggle}
            >
              Create link
            </button>
          </div>
        </div>

        {isLinksLoaded ? (
          empty ? (
            <div className="dark:text-white">no links found</div>
          ) : (
            <div className="flex-1 min-h-0 w-full">
              <div className="w-full">
                <ListLinks
                  links={links}
                  number={number}
                  numberOfElements={numberOfElements}
                  first={first}
                  last={last}
                  totalElements={totalElements}
                  totalPages={totalPages}
                  handleNumberChange={handleNumberChange}
                  size={size}
                />
              </div>
            </div>
          )
        ) : (
          <div className="flex-1 min-h-0 flex justify-center items-center w-full">
            <div className="w-full">
              <RotatingLoader />
            </div>
          </div>
        )}
      </div>

      {isCreateLinkModalOpen && (
        <CreateLink
          isCreateLinkModalOpen={isCreateLinkModalOpen}
          handleLinkCreateModalToggle={handleLinkCreateModalToggle}
          handleLinkReload={handleLinkReload}
        />
      )}
    </div>
  );
};

export default LinksPage;
