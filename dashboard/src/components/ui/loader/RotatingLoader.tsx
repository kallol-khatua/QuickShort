import { Loader } from "lucide-react";
import { motion } from "framer-motion";

export default function RotatingLoader() {
  return (
    <div className="flex-1 flex justify-center items-center h-full">
      <div className="flex">
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ repeat: Infinity, duration: 1.4, ease: "linear" }}
        >
          <Loader className="w-10 h-10 text-blue-500" />
        </motion.div>
      </div>
    </div>
  );
}
