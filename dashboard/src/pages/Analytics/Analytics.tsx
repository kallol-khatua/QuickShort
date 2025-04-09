import { useEffect, useState } from "react";
import PageBreadcrumb from "../../components/common/PageBreadCrumb";
import { MousePointerClick } from "lucide-react";
import axios from "axios";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/store";

// const clicksPerUrl = [
//   {
//     originalUrl: "https://www.geeksforgeeks.org/courses/gfg-160-series",
//     shortCode: "1Q2BD3KPbZ",
//     totalClicks: 4,
//   },
//   {
//     originalUrl: "https://www.geeksforgeeks.org/problem-of-the-day",
//     shortCode: "Sv394Vya1I",
//     totalClicks: 2,
//   },
//   {
//     originalUrl: "https://github.com/kallol-khatua/",
//     shortCode: "wtpRN61rhA",
//     totalClicks: 4,
//   },
// ];

interface Click {
  originalUrl: string;
  shortCode: string;
  totalClicks: number;
}

const UrlStat: React.FC<{
  clicksPerUrl: Click[];
}> = ({ clicksPerUrl }) => {
  const [linkType, setLinkType] = useState("shortCode");

  return (
    <div className="rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.02] dark:text-white p-2 ">
      {/* Toggle Switch */}
      <div className="flex justify-between items-center">
        <div className="flex items-center p-1 rounded-full">
          <button
            onClick={() => setLinkType("shortCode")}
            className={`px-4 py-2 text-sm font-medium rounded-full transition ${
              linkType === "shortCode"
                ? "bg-gray-200 dark:bg-gray-700 text-gray-900 dark:text-white shadow-md"
                : "text-gray-600 dark:text-gray-300"
            }`}
          >
            Short Links
          </button>

          <button
            onClick={() => setLinkType("originalUrl")}
            className={`px-4 py-2 text-sm font-medium rounded-full transition ${
              linkType === "originalUrl"
                ? "bg-gray-200 dark:bg-gray-700 text-gray-900 dark:text-white shadow-md"
                : "text-gray-600 dark:text-gray-300"
            }`}
          >
            Destination URLs
          </button>
        </div>

        <div className="flex gap-1">
          <MousePointerClick className="h-5" /> Clicks
        </div>
      </div>

      {/* Data */}
      {clicksPerUrl.map((click) => (
        <div
          className="flex justify-between mt-2 mb-2 border  border-t-1 border-l-0 border-r-0 border-b-0"
          key={click.shortCode}
        >
          <div>{click[linkType as keyof typeof click]}</div>
          <div>{click.totalClicks}</div>
        </div>
      ))}
    </div>
  );
};

const Analytics: React.FC = () => {
  const [clicksPerUrl, setClicksPerUrl] = useState([]);

  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );

  useEffect(() => {
    const fetchData = async (): Promise<void> => {
      try {
        const url = `${
          import.meta.env.VITE_ANALYTICS_BACKEND_BASE_URL
        }/report/${
          currentWorkspace?.workspaceId.id
        }?from=2025-04-02&to=2025-04-20`;

        const response = await axios.get(url);

        setTimeout(() => {
          setClicksPerUrl(response.data.data.clicksPerUrl);
        }, 500);
        // toast.success("Order cancelled successfully!");
        // handleReload();
      } catch (err: unknown) {
        if (axios.isAxiosError(err) && err.response) {
          //   const errorData: ErrorApiResponse = err.response.data;
          //   toast.error(errorData.message);

          //   // if unauthorized the logout using auth slice, protected route will take to signin page
          //   if (errorData.status_code === 401) {
          //     dispatch(logout());
          //   }
          // } else {
          console.error("Unexpected error:", err);
        }

        return;
      }
    };

    fetchData();
  }, []);

  return (
    <div className="min-h-full flex flex-col">
      {/* Page Header */}
      <PageBreadcrumb pageTitle="Analytics" />

      <div className="grid grid-cols-1 xl:grid-cols-2 gap-4">
        <UrlStat clicksPerUrl={clicksPerUrl} />
        {/* <UrlStat /> */}
      </div>
    </div>
  );
};

export default Analytics;
