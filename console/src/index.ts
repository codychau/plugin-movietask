import "./styles/tailwind.css";
import { definePlugin } from "@halo-dev/console-shared";
import PhotoList from "@/views/PhotoList.vue";
import { markRaw } from "vue";
import RiImage2Line from "~icons/ri/image-2-line";

export default definePlugin({
  routes: [
    {
      parentName: "Root",
      route: {
        path: "/movietask",
        name: "MovieTask",
        component: PhotoList,
        meta: {
          permissions: ["plugin:movietask:view"],
          menu: {
            name: "影库",
            group: "content",
            icon: markRaw(RiImage2Line),
          },
        },
      },
    },
  ],
});
