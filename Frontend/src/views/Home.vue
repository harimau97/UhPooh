// views/HomeView.vue
<script></script>
<template>
  <!-- <header
    class="flex justify-between items-center p-4 text-white bg-cyan-600 header"
  >
    <div class="flex items-center">
      <div class="relative message-count">
        <img class="w-8 h-8 message-icon" src="@/assets/message.png" alt="" />
        <div
          v-if="messageCount > 0"
          class="absolute top-0 right-0 w-5 h-5 text-xs text-center text-white bg-green-500 rounded-full current-count"
        >
          {{ messageCount }}
        </div>
      </div>
      <div class="ml-4 font-semibold nickname">{{ sbUserInfo?.nickname }}</div>
    </div>
  </header> -->

  <main>
    <div class="overflow-x-hidden">
      <!-- Main Section -->
      <section class="relative h-screen bg-center bg-cover">
        <div class="absolute inset-0">
          <video
            src="@/assets/swimming.mp4"
            autoplay
            loop
            muted
            class="overflow-hidden w-full h-full scale-[1.15]"
          ></video>
        </div>
        <div
          class="absolute left-10 top-1/2 p-6 bg-white rounded-lg shadow-lg transform -translate-y-1/2"
        >
          <h1 class="mb-4 text-4xl font-bold">Swimming Season Is On</h1>
          <p class="mb-4">
            I’m a paragraph. Click here to add your own text and edit me. Let
            your users get to know you.
          </p>
          <button class="px-4 py-2 text-white bg-blue-900 rounded-md">
            Book Now
          </button>
        </div>
      </section>

      <!-- Activities Section -->
      <section id="activities" class="py-20 text-center bg-white">
        <h2 class="mb-6 text-3xl font-bold">Aquatic Activities</h2>
        <p class="mb-10">Book Your Slot Today</p>
        <div class="flex justify-center space-x-10">
          <div class="p-4 w-1/3 bg-gray-100 rounded-lg shadow-lg">
            <svg
              viewBox="0 0 800 400"
              class="object-cover mb-4 w-full h-48 rounded-md"
            >
              <rect width="800" height="400" fill="#87CEEB" />
            </svg>
            <h3 class="mb-2 text-xl font-semibold">Recreational Swims</h3>
            <p class="text-gray-600">
              I’m a paragraph. Click here to add your own text and edit me. Let
              your users get to know you.
            </p>
          </div>
          <div class="p-4 w-1/3 bg-gray-100 rounded-lg shadow-lg">
            <svg
              viewBox="0 0 800 400"
              class="object-cover mb-4 w-full h-48 rounded-md"
            >
              <rect width="800" height="400" fill="#4682B4" />
            </svg>
            <h3 class="mb-2 text-xl font-semibold">Classes & Lap Swims</h3>
            <p class="text-gray-600">
              I’m a paragraph. Click here to add your own text and edit me. Let
              your users get to know you.
            </p>
          </div>
        </div>
        <button class="px-4 py-2 mt-10 text-white bg-blue-900 rounded-md">
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
        <div class="ml-10 w-1/3">
          <h2 class="mb-4 text-3xl font-bold">We Care About Your Safety</h2>
          <p class="mb-4 text-gray-600">
            I’m a paragraph. Click here to add your own text and edit me. It’s
            easy. Just click "Edit Text" or double click me to add your own
            content and make changes to the font.
          </p>
          <button class="px-4 py-2 text-white bg-blue-900 rounded-md">
            Discover More
          </button>
        </div>
      </section>

      <!-- Events Section -->
      <section class="flex justify-center items-center py-20 bg-white">
        <div class="mr-10 w-1/3">
          <h2 class="mb-4 text-3xl font-bold">
            Plan Your Private Event at the Pool
          </h2>
          <p class="mb-4 text-gray-600">
            I’m a paragraph. Click here to add your own text and edit me. It’s
            easy. Just click "Edit Text" or double click me to add your own
            content and make changes to the font.
          </p>
          <button class="px-4 py-2 text-white bg-blue-900 rounded-md">
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
      <footer class="py-10 text-white bg-gray-800">
        <div class="container grid grid-cols-2 mx-auto">
          <div>
            <h3 class="text-lg font-bold">Welland Pool</h3>
            <p>Tel: 123-456-7890<br />Email: info@mysite.com</p>
            <p>500 Terry Francine Street, San Francisco, CA 94158</p>
            <div class="flex mt-4 space-x-4">
              <a href="#" class="hover:underline">Facebook</a>
              <a href="#" class="hover:underline">Twitter</a>
              <a href="#" class="hover:underline">Instagram</a>
            </div>
          </div>
          <div class="space-y-2 text-right">
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
