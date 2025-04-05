const HomePage = () => {
  return (
    <div className="min-h-screen flex flex-col">
      {/* Header */}
      <header className="bg-white shadow-md py-4 px-8 flex justify-between items-center">
        <h1 className="text-2xl font-bold text-blue-600">QuickShort</h1>
        <nav className="space-x-4">
          <a href="#" className="text-gray-700 hover:text-blue-600">Home</a>
          <a href="#" className="text-gray-700 hover:text-blue-600">Features</a>
          <a href="#" className="text-gray-700 hover:text-blue-600">Contact</a>
        </nav>
      </header>

      {/* Hero Section */}
      <main className="flex-grow flex flex-col items-center justify-center bg-gray-50 px-4 text-center">
        <h2 className="text-4xl md:text-5xl font-bold mb-6 text-gray-800 max-w-2xl">
          Simplify Your Links with <span className="text-blue-600">QuickShort</span>
        </h2>
        <p className="text-gray-600 mb-8 max-w-xl">
          Enter your long URLs and get clean, shareable short links in seconds.
        </p>

        {/* URL Shortener Input */}
        <div className="w-full max-w-xl bg-white rounded-xl shadow-lg p-6 flex gap-4">
          <input
            type="url"
            placeholder="Enter your long URL here..."
            className="flex-grow px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition">
            Shorten
          </button>
        </div>
      </main>

      {/* Features Section */}
      <section className="py-16 bg-white">
        <div className="max-w-5xl mx-auto px-4 text-center">
          <h3 className="text-3xl font-semibold text-gray-800 mb-6">Why Use Shortly?</h3>
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            <div className="p-6 rounded-lg border">
              <h4 className="text-xl font-semibold text-blue-600 mb-2">Fast & Reliable</h4>
              <p className="text-gray-600">Shorten your URLs in an instant with 99.9% uptime.</p>
            </div>
            <div className="p-6 rounded-lg border">
              <h4 className="text-xl font-semibold text-blue-600 mb-2">Custom Aliases</h4>
              <p className="text-gray-600">Choose your own aliases to make URLs memorable.</p>
            </div>
            <div className="p-6 rounded-lg border">
              <h4 className="text-xl font-semibold text-blue-600 mb-2">Analytics</h4>
              <p className="text-gray-600">Track clicks and performance of your shortened links with visual reports.</p>
            </div>
            <div className="p-6 rounded-lg border">
              <h4 className="text-xl font-semibold text-blue-600 mb-2">Link Management</h4>
              <p className="text-gray-600">Organize and manage your links in one place for better efficiency.</p>
            </div>
            <div className="p-6 rounded-lg border">
              <h4 className="text-xl font-semibold text-blue-600 mb-2">Team Workspaces</h4>
              <p className="text-gray-600">Create collaborative workspaces to manage links with your team.</p>
            </div>
            <div className="p-6 rounded-lg border">
              <h4 className="text-xl font-semibold text-blue-600 mb-2">Group Monitoring</h4>
              <p className="text-gray-600">Maintain and monitor group performance with shared analytics and access.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-100 py-6 mt-auto">
        <div className="max-w-5xl mx-auto px-4 flex flex-col md:flex-row justify-between items-center text-gray-600">
          <p>&copy; {new Date().getFullYear()} Shortly. All rights reserved.</p>
          <div className="space-x-4 mt-2 md:mt-0">
            <a href="#" className="hover:text-blue-600">Privacy Policy</a>
            <a href="#" className="hover:text-blue-600">Terms of Service</a>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default HomePage;
