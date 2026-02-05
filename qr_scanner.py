import cv2
import customtkinter as ctk
from PIL import Image
import numpy as np
import threading
import time
import webbrowser
from pyzbar.pyzbar import decode
import qrcode
from tkinter import filedialog, colorchooser

# Set theme
ctk.set_appearance_mode("Dark")
ctk.set_default_color_theme("blue")

class ModernQRScannerApp(ctk.CTk):
    def __init__(self):
        super().__init__()

        self.title("QR 코드 스캐너 & 생성기 (Advanced)")
        self.geometry("900x800")
        
        # Handle window closing
        self.protocol("WM_DELETE_WINDOW", self.on_closing)

        # Tab View
        self.tab_view = ctk.CTkTabview(self, command=self.on_tab_change) # Add callback
        self.tab_view.pack(fill="both", expand=True, padx=20, pady=20)

        self.tab_scan = self.tab_view.add("스캔 (Scan)")
        self.tab_create = self.tab_view.add("생성 (Create)")

        # Initialize State
        self.vid = None
        self.is_running = True
        self.is_scanning = True
        self.scanned_data = None
        self.camera_paused = False # Flag for tab switching

        # Initialize Tabs
        self.setup_scan_tab()
        self.setup_create_tab()
        
        # Start Scanner Thread
        self.start_camera_thread()

    def setup_scan_tab(self):
        # ... (Existing Scan UI Setup - kept mostly same) ...
        self.tab_scan.grid_columnconfigure(0, weight=1)
        self.tab_scan.grid_rowconfigure(1, weight=1)

        # Top Control Bar
        control_frame = ctk.CTkFrame(self.tab_scan, fg_color="transparent")
        control_frame.grid(row=0, column=0, padx=10, pady=(10, 5), sticky="ew")

        # Camera Selection
        self.available_cameras = self.get_available_cameras()
        self.camera_selector = ctk.CTkOptionMenu(
            control_frame, 
            values=self.available_cameras,
            command=self.change_camera
        )
        self.camera_selector.pack(side="left")
        
        if self.available_cameras:
            self.camera_selector.set(self.available_cameras[0])
            self.current_camera_index = int(self.available_cameras[0].split(" ")[1])
        else:
            self.camera_selector.set("카메라 없음")
            self.current_camera_index = -1

        self.status_label = ctk.CTkLabel(control_frame, text="스캔 준비 완료", text_color="gray")
        self.status_label.pack(side="right", padx=10)

        # Auto Open
        self.auto_open_var = ctk.BooleanVar(value=True)
        self.auto_open_switch = ctk.CTkSwitch(
            control_frame, 
            text="자동 열기", 
            variable=self.auto_open_var, 
            progress_color="green"
        )
        self.auto_open_switch.pack(side="right")

        # Video Area
        self.video_frame = ctk.CTkFrame(self.tab_scan, fg_color="#1a1a1a", corner_radius=15)
        self.video_frame.grid(row=1, column=0, padx=10, pady=10, sticky="nsew")
        
        self.video_label = ctk.CTkLabel(self.video_frame, text="카메라가 일시 중지되었습니다.", bg_color="transparent")
        self.video_label.place(relx=0.5, rely=0.5, anchor="center")

        # Bottom Area
        self.bottom_frame = ctk.CTkFrame(self.tab_scan, height=150, corner_radius=15)
        self.bottom_frame.grid(row=2, column=0, padx=10, pady=(5, 10), sticky="ew")
        self.bottom_frame.grid_propagate(False)

        self.result_text = ctk.CTkLabel(
            self.bottom_frame, 
            text="스캔 중...", 
            font=("Malgun Gothic", 16, "bold"),
            wraplength=800
        )
        self.result_text.pack(pady=(20, 10))

        action_frame = ctk.CTkFrame(self.bottom_frame, fg_color="transparent")
        action_frame.pack(pady=5)

        self.copy_btn = ctk.CTkButton(action_frame, text="내용 복사", command=self.copy_to_clip, state="disabled")
        self.copy_btn.pack(side="left", padx=10)

        self.open_btn = ctk.CTkButton(action_frame, text="링크 열기", command=self.open_link, fg_color="green", hover_color="darkgreen", state="disabled")
        self.open_btn.pack(side="left", padx=10)

        self.resume_btn = ctk.CTkButton(action_frame, text="다시 스캔", command=self.resume_scanning, fg_color="gray", hover_color="darkgray", state="disabled")
        self.resume_btn.pack(side="left", padx=10)

    def setup_create_tab(self):
        self.tab_create.grid_columnconfigure(0, weight=1)
        
        # Input Area
        input_frame = ctk.CTkFrame(self.tab_create, fg_color="transparent")
        input_frame.pack(fill="x", padx=20, pady=20)
        
        ctk.CTkLabel(input_frame, text="QR 코드 내용 입력:", font=("Malgun Gothic", 14)).pack(anchor="w")
        
        self.qr_input = ctk.CTkEntry(input_frame, placeholder_text="https:// or text...", height=40)
        self.qr_input.pack(fill="x", pady=10)

        # Options Frame
        options_frame = ctk.CTkFrame(input_frame, fg_color="transparent")
        options_frame.pack(fill="x", pady=5)

        # Color Picker
        self.qr_color = "black"
        self.color_btn = ctk.CTkButton(options_frame, text="색상 선택 (Color)", command=self.pick_color, width=150, fg_color="gray")
        self.color_btn.pack(side="left", padx=(0, 10))

        # Logo Picker
        self.logo_path = None
        self.logo_btn = ctk.CTkButton(options_frame, text="로고 추가 (Logo)", command=self.pick_logo, width=150, fg_color="gray")
        self.logo_btn.pack(side="left")
        
        # Generate Button
        self.generate_btn = ctk.CTkButton(
            input_frame, 
            text="QR 코드 생성하기 (Generate)", 
            command=self.generate_qr,
            height=40,
            fg_color="#3B8ED0",
            font=("Malgun Gothic", 14, "bold")
        )
        self.generate_btn.pack(fill="x", pady=10)

        # Preview Area
        self.preview_frame = ctk.CTkFrame(self.tab_create, fg_color="#1a1a1a", corner_radius=15, height=350)
        self.preview_frame.pack(fill="both", expand=True, padx=20, pady=10)
        
        self.qr_preview_label = ctk.CTkLabel(self.preview_frame, text="내용을 입력하고 생성 버튼을 누르세요", text_color="gray")
        self.qr_preview_label.place(relx=0.5, rely=0.5, anchor="center")

        # Save Button
        self.save_btn = ctk.CTkButton(
            self.tab_create, 
            text="이미지 저장 (Save)", 
            command=self.save_qr_image,
            state="disabled",
            fg_color="green",
            font=("Malgun Gothic", 14, "bold")
        )
        self.save_btn.pack(pady=20)
        
        self.generated_qr_image = None

    # --- Tab Handling for Resource Optimization ---
    def on_tab_change(self):
        current_tab = self.tab_view.get()
        if current_tab == "스캔 (Scan)":
            self.camera_paused = False
            self.status_label.configure(text="카메라 연결 중...")
        else:
            self.camera_paused = True
            # We don't immediately release in main thread to avoid lock, 
            # let the thread handle release based on flag
            
    # --- Scanner Logic ---
    def get_available_cameras(self):
        cameras = []
        for i in range(3):
            cap = cv2.VideoCapture(i, cv2.CAP_DSHOW)
            if cap.isOpened():
                cameras.append(f"Camera {i}")
                cap.release()
        if not cameras: cameras = ["Camera 0"]
        return cameras

    def start_camera_thread(self):
        if self.current_camera_index == -1: return
        self.thread = threading.Thread(target=self.video_loop, daemon=True)
        self.thread.start()

    def change_camera(self, selection):
        self.current_camera_index = int(selection.split(" ")[1])
        if self.vid and self.vid.isOpened(): self.vid.release()

    def video_loop(self):
        last_index = -1
        
        while self.is_running:
            # Resource Check
            if self.camera_paused:
                if self.vid and self.vid.isOpened():
                    self.vid.release() # Release resource
                    self.after(0, lambda: self.video_label.configure(image=None, text="카메라가 일시 중지되었습니다."))
                time.sleep(0.5)
                continue

            # Initialization / Switch
            if self.current_camera_index != last_index or (self.vid is None or not self.vid.isOpened()):
                if self.vid: self.vid.release()
                self.vid = cv2.VideoCapture(self.current_camera_index, cv2.CAP_DSHOW)
                self.vid.set(cv2.CAP_PROP_FRAME_WIDTH, 1280)
                self.vid.set(cv2.CAP_PROP_FRAME_HEIGHT, 720)
                last_index = self.current_camera_index
            
            if not self.vid.isOpened():
                time.sleep(1)
                continue

            ret, frame = self.vid.read()
            if ret:
                if self.is_scanning:
                    self.detect_qr(frame)
                
                # Update UI only if in Scan tab
                if not self.camera_paused:
                    cv2image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGBA)
                    img = Image.fromarray(cv2image)
                    ctk_image = ctk.CTkImage(light_image=img, dark_image=img, size=(640, 480))
                    self.after(0, self.update_video_label, ctk_image)
            else:
                 time.sleep(0.1)

    def update_video_label(self, ctk_image):
        if self.is_running and not self.camera_paused:
            self.video_label.configure(image=ctk_image, text="")
            self.video_label.image = ctk_image 

    def detect_qr(self, frame):
        try:
            decoded_objects = decode(frame)
            for obj in decoded_objects:
                data = obj.data.decode('utf-8')
                if data:
                    self.scanned_data = data
                    self.is_scanning = False 
                    self.after(0, self.update_ui_on_scan)
                    break 
        except: pass

    def update_ui_on_scan(self):
        self.result_text.configure(text=f"감지됨: {self.scanned_data}", text_color="#2CC985")
        self.status_label.configure(text="스캔 일시 중지됨")
        
        # Auto Copy
        self.copy_to_clip()
        self.copy_btn.configure(state="normal")
        self.resume_btn.configure(state="normal")
        if self.scanned_data.startswith("http"):
            self.open_btn.configure(state="normal", text="링크 열기")
            if self.auto_open_var.get(): self.open_link()
        else:
             self.open_btn.configure(state="normal", text="공유")

    def resume_scanning(self):
        self.scanned_data = None
        self.is_scanning = True
        self.result_text.configure(text="스캔 중...", text_color="gray")
        self.status_label.configure(text="스캔 진행 중")
        self.copy_btn.configure(state="disabled")
        self.open_btn.configure(state="disabled")
        self.resume_btn.configure(state="disabled")

    def copy_to_clip(self):
        if self.scanned_data:
            self.clipboard_clear()
            self.clipboard_append(self.scanned_data)
            self.status_label.configure(text="클립보드에 복사되었습니다!")

    def open_link(self):
        if self.scanned_data and self.scanned_data.startswith("http"):
            webbrowser.open(self.scanned_data)

    # --- Generator Logic ---
    def pick_color(self):
        color = colorchooser.askcolor(title="QR 코드 색상 선택")
        if color[1]:
            self.qr_color = color[1]
            self.color_btn.configure(fg_color=self.qr_color, text=f"색상: {self.qr_color}")

    def pick_logo(self):
        file_path = filedialog.askopenfilename(filetypes=[("Image Files", "*.png;*.jpg;*.jpeg")])
        if file_path:
            self.logo_path = file_path
            self.logo_btn.configure(text="로고 선택됨 (Change)", fg_color="green")

    def generate_qr(self):
        text = self.qr_input.get()
        if not text: return
            
        # Create QR
        qr = qrcode.QRCode(
            version=1,
            error_correction=qrcode.constants.ERROR_CORRECT_H, # High error correction for logo
            box_size=10,
            border=4,
        )
        qr.add_data(text)
        qr.make(fit=True)

        self.generated_qr_image = qr.make_image(fill_color=self.qr_color, back_color="white").convert('RGB')
        
        # Add Logo
        if self.logo_path:
            try:
                logo = Image.open(self.logo_path)
                
                # Calculate size (20% of QR size)
                qr_width, qr_height = self.generated_qr_image.size
                logo_size = int(qr_width * 0.2)
                logo = logo.resize((logo_size, logo_size), Image.Resampling.LANCZOS)
                
                # Calculate position (Center)
                pos = ((qr_width - logo_size) // 2, (qr_height - logo_size) // 2)
                
                self.generated_qr_image.paste(logo, pos)
            except Exception as e:
                print(f"Logo Error: {e}")

        # Display
        display_img = ctk.CTkImage(light_image=self.generated_qr_image, dark_image=self.generated_qr_image, size=(300, 300))
        self.qr_preview_label.configure(image=display_img, text="")
        self.save_btn.configure(state="normal")

    def save_qr_image(self):
        if self.generated_qr_image:
            file_path = filedialog.asksaveasfilename(defaultextension=".png", filetypes=[("PNG files", "*.png")])
            if file_path: self.generated_qr_image.save(file_path)

    def on_closing(self):
        self.is_running = False
        self.camera_paused = True # Stop using camera
        if self.vid and self.vid.isOpened():
            self.vid.release()
        self.destroy()

if __name__ == "__main__":
    app = ModernQRScannerApp()
    app.mainloop()
