import { useEffect, useRef, useState } from "react";
import PageBreadcrumb from "../../components/common/PageBreadCrumb";
import { MousePointerClick } from "lucide-react";
import axios from "axios";
import { useSelector } from "react-redux";
import { RootState } from "../../redux/store";

import { DateRange } from "react-date-range";
import "react-date-range/dist/styles.css"; // main style file
import "react-date-range/dist/theme/default.css"; // theme css file
import { format } from "date-fns";
import RotatingLoader from "../../components/ui/loader/RotatingLoader";

interface Click {
  originalUrl: string;
  shortCode: string;
  totalClicks: number;
}

interface GeneralStat {
  label: string;
  total: number;
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
      {/* short url */}
      {linkType === "shortCode" &&
        (clicksPerUrl.length > 0 ? (
          clicksPerUrl.map((click) => (
            <div
              className="flex justify-between mt-2 mb-2 border  border-t-1 border-l-0 border-r-0 border-b-0 dark:border-white/[0.05]"
              key={click.shortCode}
            >
              <div className="truncate pr-15">{`${
                import.meta.env.VITE_URL_TRACKING_BASE_URL
              }/${click.shortCode}`}</div>
              <div>{click.totalClicks}</div>
            </div>
          ))
        ) : (
          <div className="py-4 pl-2">No data available</div>
        ))}

      {/* Original url */}
      {linkType === "originalUrl" &&
        (clicksPerUrl.length > 0 ? (
          clicksPerUrl.map((click) => (
            <div
              className="flex justify-between mt-2 mb-2 border  border-t-1 border-l-0 border-r-0 border-b-0 dark:border-white/[0.05]"
              key={click.shortCode}
            >
              <div className="truncate pr-15">{click.originalUrl}</div>
              <div>{click.totalClicks}</div>
            </div>
          ))
        ) : (
          <div className="py-4 pl-2">No data available</div>
        ))}
    </div>
  );
};

const GeneralStat: React.FC<{
  deviceStats: GeneralStat[];
  osStats: GeneralStat[];
  browserStats: GeneralStat[];
}> = ({ browserStats, osStats, deviceStats }) => {
  const [statType, setStatType] = useState("deviceStats"); // deviceStats, osStats, browserStats

  return (
    <div className="rounded-xl border border-gray-200 bg-white dark:border-white/[0.05] dark:bg-white/[0.02] dark:text-white p-2 ">
      {/* Toggle Switch */}
      <div className="flex justify-between items-center">
        <div className="flex items-center p-1 rounded-full">
          <button
            onClick={() => setStatType("deviceStats")}
            className={`px-4 py-2 text-sm font-medium rounded-full transition ${
              statType === "deviceStats"
                ? "bg-gray-200 dark:bg-gray-700 text-gray-900 dark:text-white shadow-md"
                : "text-gray-600 dark:text-gray-300"
            }`}
          >
            Device
          </button>

          <button
            onClick={() => setStatType("osStats")}
            className={`px-4 py-2 text-sm font-medium rounded-full transition ${
              statType === "osStats"
                ? "bg-gray-200 dark:bg-gray-700 text-gray-900 dark:text-white shadow-md"
                : "text-gray-600 dark:text-gray-300"
            }`}
          >
            Os
          </button>

          <button
            onClick={() => setStatType("browserStats")}
            className={`px-4 py-2 text-sm font-medium rounded-full transition ${
              statType === "browserStats"
                ? "bg-gray-200 dark:bg-gray-700 text-gray-900 dark:text-white shadow-md"
                : "text-gray-600 dark:text-gray-300"
            }`}
          >
            Browser
          </button>
        </div>

        <div className="flex gap-1">
          <MousePointerClick className="h-5" /> Clicks
        </div>
      </div>

      {statType === "browserStats" &&
        (browserStats.length > 0 ? (
          browserStats.map((click) => (
            <div
              className="flex justify-between mt-2 mb-2 border  border-t-1 border-l-0 border-r-0 border-b-0 dark:border-white/[0.05]"
              key={click.label}
            >
              <div>{click.label}</div>
              <div>{click.total}</div>
            </div>
          ))
        ) : (
          <div className="py-4 pl-2">No data available</div>
        ))}

      {statType === "osStats" &&
        (osStats.length > 0 ? (
          osStats.map((click) => (
            <div
              className="flex justify-between mt-2 mb-2 border  border-t-1 border-l-0 border-r-0 border-b-0 dark:border-white/[0.05]"
              key={click.label}
            >
              <div>{click.label}</div>
              <div>{click.total}</div>
            </div>
          ))
        ) : (
          <div className="py-4 pl-2">No data available</div>
        ))}

      {statType === "deviceStats" &&
        (deviceStats.length > 0 ? (
          deviceStats.map((click) => (
            <div
              className="flex justify-between mt-2 mb-2 border  border-t-1 border-l-0 border-r-0 border-b-0 dark:border-white/[0.05]"
              key={click.label}
            >
              <div>{click.label}</div>
              <div>{click.total}</div>
            </div>
          ))
        ) : (
          <div className="py-4 pl-2">No data available</div>
        ))}
    </div>
  );
};

const Analytics: React.FC = () => {
  const [clicksPerUrl, setClicksPerUrl] = useState([]);
  const [deviceStats, setDeviceStats] = useState([]);
  const [osStats, setOsStats] = useState([]);
  const [browserStats, setBrowserStats] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  const currentWorkspace = useSelector(
    (state: RootState) => state.workspace.currentWorkspace
  );

  const [range, setRange] = useState([
    {
      startDate: new Date(),
      endDate: new Date(),
      key: "selection",
    },
  ]);
  const [showCalendar, setShowCalendar] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) {
        setShowCalendar(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // fetch data from backend initially when the page loads and when user change date range
  useEffect(() => {
    const fetchData = async (): Promise<void> => {
      try {
        const url = `${
          import.meta.env.VITE_ANALYTICS_BACKEND_BASE_URL
        }/report/${currentWorkspace?.workspaceId.id}?from=${format(
          range[0].startDate,
          "yyyy-MM-dd"
        )}&to=${format(range[0].endDate, "yyyy-MM-dd")}`;

        const response = await axios.get(url);

        setClicksPerUrl(response.data.data.clicksPerUrl);
        setDeviceStats(response.data.data.deviceStats);
        setBrowserStats(response.data.data.browserStats);
        setOsStats(response.data.data.osStats);

        setTimeout(() => {
          setIsLoading(false);
        }, 500);
      } catch (err: unknown) {
        console.error("Unexpected error:", err);

        return;
      }
    };

    fetchData();
  }, [currentWorkspace?.workspaceId.id, range]);

  return (
    <div className="min-h-full flex flex-col">
      {/* Page Header */}
      <PageBreadcrumb pageTitle="Analytics" />

      {/* Date picker */}

      <div className="relative w-full max-w-md mb-4">
        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
          Select Date
        </label>
        <input
          readOnly
          onClick={() => setShowCalendar(!showCalendar)}
          value={`${format(range[0].startDate, "yyyy-MM-dd")} to ${format(
            range[0].endDate,
            "yyyy-MM-dd"
          )}`}
          className="cursor-pointer px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 dark:bg-gray-800 dark:text-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          placeholder="date"
        />

        {showCalendar && (
          <div
            ref={ref}
            className="absolute z-10 mt-2 shadow-lg border border-gray-200 dark:border-gray-700 rounded-lg overflow-hidden bg-white dark:bg-gray-900"
          >
            <DateRange
              ranges={range}
              onChange={(item) =>
                setRange([
                  {
                    startDate: item.selection.startDate || new Date(),
                    endDate: item.selection.endDate || new Date(),
                    key: "selection",
                  },
                ])
              }
              moveRangeOnFirstSelection={false}
              editableDateInputs={true}
            />
          </div>
        )}
      </div>

      {!isLoading ? (
        <div className="grid grid-cols-1 xl:grid-cols-2 gap-4">
          <UrlStat clicksPerUrl={clicksPerUrl} />

          <GeneralStat
            browserStats={browserStats}
            osStats={osStats}
            deviceStats={deviceStats}
          />
        </div>
      ) : (
        <div className="flex-1 min-h-0 flex justify-center items-center w-full">
          <div className="w-full">
            <RotatingLoader />
          </div>
        </div>
      )}
    </div>
  );
};

export default Analytics;
