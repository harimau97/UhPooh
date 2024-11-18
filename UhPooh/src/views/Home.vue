// views/HomeView.vue
<script></script>
<template>
  <!-- <header
    class="header bg-cyan-600 text-white flex items-center justify-between p-4"
  >
    <div class="flex items-center">
      <div class="message-count relative">
        <img class="message-icon w-8 h-8" src="@/assets/message.png" alt="" />
        <div
          v-if="messageCount > 0"
          class="current-count absolute top-0 right-0 bg-green-500 text-white rounded-full w-5 h-5 text-center text-xs"
        >
          {{ messageCount }}
        </div>
      </div>
      <div class="nickname ml-4 font-semibold">{{ sbUserInfo?.nickname }}</div>
    </div>
  </header> -->

  <main>
    <div class="overflow-x-hidden">
      <!-- Main Section -->
      <section class="relative h-screen bg-cover bg-center">
        <div class="absolute inset-0">
          <svg viewBox="0 0 800 400" class="w-full h-full object-cover">
            <rect width="800" height="400" fill="#00BFFF" />
          </svg>
        </div>
        <div
          class="absolute top-1/2 left-10 transform -translate-y-1/2 bg-white p-6 rounded-lg shadow-lg"
        >
          <h1 class="text-4xl font-bold mb-4">Swimming Season Is On</h1>
          <p class="mb-4">
            I’m a paragraph. Click here to add your own text and edit me. Let
            your users get to know you.
          </p>
          <button class="bg-blue-900 text-white px-4 py-2 rounded-md">
            Book Now
          </button>
        </div>
      </section>

      <!-- Activities Section -->
      <section id="activities" class="py-20 bg-white text-center">
        <h2 class="text-3xl font-bold mb-6">Aquatic Activities</h2>
        <p class="mb-10">Book Your Slot Today</p>
        <div class="flex justify-center space-x-10">
          <div class="w-1/3 bg-gray-100 p-4 rounded-lg shadow-lg">
            <svg
              viewBox="0 0 800 400"
              class="w-full h-48 object-cover rounded-md mb-4"
            >
              <rect width="800" height="400" fill="#87CEEB" />
            </svg>
            <h3 class="text-xl font-semibold mb-2">Recreational Swims</h3>
            <p class="text-gray-600">
              I’m a paragraph. Click here to add your own text and edit me. Let
              your users get to know you.
            </p>
          </div>
          <div class="w-1/3 bg-gray-100 p-4 rounded-lg shadow-lg">
            <svg
              viewBox="0 0 800 400"
              class="w-full h-48 object-cover rounded-md mb-4"
            >
              <rect width="800" height="400" fill="#4682B4" />
            </svg>
            <h3 class="text-xl font-semibold mb-2">Classes & Lap Swims</h3>
            <p class="text-gray-600">
              I’m a paragraph. Click here to add your own text and edit me. Let
              your users get to know you.
            </p>
          </div>
        </div>
        <button class="mt-10 bg-blue-900 text-white px-4 py-2 rounded-md">
          Book Now
        </button>
      </section>

      <!-- Safety Section -->
      <section class="flex justify-center items-center py-20 bg-gray-100">
        <div class="w-1/2">
          <svg viewBox="0 0 800 400" class="w-full h-auto rounded-md shadow-lg">
            <rect width="800" height="400" fill="#00CED1" />
          </svg>
        </div>
        <div class="w-1/3 ml-10">
          <h2 class="text-3xl font-bold mb-4">We Care About Your Safety</h2>
          <p class="text-gray-600 mb-4">
            I’m a paragraph. Click here to add your own text and edit me. It’s
            easy. Just click "Edit Text" or double click me to add your own
            content and make changes to the font.
          </p>
          <button class="bg-blue-900 text-white px-4 py-2 rounded-md">
            Discover More
          </button>
        </div>
      </section>

      <!-- Events Section -->
      <section class="flex justify-center items-center py-20 bg-white">
        <div class="w-1/3 mr-10">
          <h2 class="text-3xl font-bold mb-4">
            Plan Your Private Event at the Pool
          </h2>
          <p class="text-gray-600 mb-4">
            I’m a paragraph. Click here to add your own text and edit me. It’s
            easy. Just click "Edit Text" or double click me to add your own
            content and make changes to the font.
          </p>
          <button class="bg-blue-900 text-white px-4 py-2 rounded-md">
            Contact Us
          </button>
        </div>
        <div class="w-1/2">
          <svg viewBox="0 0 800 400" class="w-full h-auto rounded-md shadow-lg">
            <rect width="800" height="400" fill="#B0E0E6" />
          </svg>
        </div>
      </section>

      <!-- Footer Section -->
      <footer class="bg-gray-800 text-white py-10">
        <div class="container mx-auto grid grid-cols-2">
          <div>
            <h3 class="text-lg font-bold">Welland Pool</h3>
            <p>Tel: 123-456-7890<br />Email: info@mysite.com</p>
            <p>500 Terry Francine Street, San Francisco, CA 94158</p>
            <div class="flex space-x-4 mt-4">
              <a href="#" class="hover:underline">Facebook</a>
              <a href="#" class="hover:underline">Twitter</a>
              <a href="#" class="hover:underline">Instagram</a>
            </div>
          </div>
          <div class="text-right space-y-2">
            <a href="#" class="hover:underline">Activities</a><br />
            <a href="#" class="hover:underline">Our Pools</a><br />
            <a href="#" class="hover:underline">Pricing</a><br />
            <a href="#" class="hover:underline">Visit Us</a><br />
            <a href="#" class="hover:underline">FAQ</a><br />
            <a href="#" class="hover:underline">Accessibility</a><br />
            <a href="#" class="hover:underline">Terms & Conditions</a><br />
            <a href="#" class="hover:underline">Privacy Policy</a><br />
            <a href="#" class="hover:underline">Shipping Policy</a><br />
            <a href="#" class="hover:underline">Refund Policy</a>
          </div>
        </div>
      </footer>
    </div>
  </main>
</template>

<!-- <script>
export default {};
</script> -->

<style scoped></style>
